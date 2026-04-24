package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import deserializer.SensorsSnapshotDeserializer;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
public class SnapshotProcessor {

    private final ScenarioRepository scenarioRepository;

    @GrpcClient("hub-router")
    private HubRouterControllerBlockingStub hubRouterClient;

    public SnapshotProcessor(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${analyzer.topics.snapshots}")
    private String snapshotsTopic;

    public void start() {
        log.info("Запуск SnapshotProcessor. Подписка на топик: {}", snapshotsTopic);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-snapshots-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        try (KafkaConsumer<String, SensorsSnapshotAvro> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(snapshotsTopic));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(200));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshot = record.value();
                    processSnapshot(snapshot);
                }

                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка в SnapshotProcessor: ", e);
        }
    }

    void processSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        log.debug("Обработка снапшота хаба {}", hubId);

        List<Scenario> scenarios = loadScenariosWhenStable(hubId);
        if (scenarios.isEmpty()) {
            log.debug("Нет сценариев для хаба {}", hubId);
            return;
        }

        for (Scenario scenario : scenarios) {
            boolean conditionsMet = checkScenarioConditions(scenario, snapshot);

            if (conditionsMet) {
                log.info("Условия сценария '{}' выполнены. Отправляем действия...", scenario.getName());
                executeScenarioActions(hubId, scenario);
            } else {
                log.debug("Условия сценария '{}' не выполнены.", scenario.getName());
            }
        }
    }

    /**
     * HubEventProcessor пишет сценарии в БД в другом потоке; между двумя подряд SCENARIO_ADDED
     * снапшот может прийти раньше, чем зафиксируется второй сценарий. Ждём стабилизации числа сценариев.
     */
    private List<Scenario> loadScenariosWhenStable(String hubId) {
        int lastSize = -1;
        int stableRounds = 0;
        List<Scenario> last = List.of();
        for (int i = 0; i < 25; i++) {
            List<Scenario> current = scenarioRepository.findByHubId(hubId);
            if (current.size() == lastSize) {
                stableRounds++;
                if (stableRounds >= 2) {
                    return current;
                }
            } else {
                stableRounds = 0;
                lastSize = current.size();
            }
            last = current;
            try {
                Thread.sleep(35);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return last;
            }
        }
        return last;
    }

    private boolean checkScenarioConditions(Scenario scenario, SensorsSnapshotAvro snapshot) {
        if (scenario.getConditions().isEmpty()) {
            log.warn("Сценарий '{}' не содержит условий!", scenario.getName());
            return false;
        }

        for (ScenarioCondition sc : scenario.getConditions()) {
            String sensorId = sc.getSensor().getId();
            SensorStateAvro state = snapshot.getSensorsState().get(sensorId);
            if (state == null) {
                log.debug("Сенсор {} отсутствует в снапшоте, пропускаем условие", sensorId);
                return false;
            }

            Condition condition = sc.getCondition();
            boolean result = evaluateCondition(condition, state.getData());
            if (!result) return false;
        }

        return true;
    }

    private boolean evaluateCondition(Condition condition, Object data) {
        ConditionTypeAvro type = condition.getType();
        ConditionOperationAvro operation = condition.getOperation();
        Integer expected = condition.getValue();
        if (expected == null) {
            return false;
        }

        return switch (type) {
            case TEMPERATURE -> {
                if (data instanceof ClimateSensorAvro c) {
                    yield compare(c.getTemperatureC(), expected, operation);
                }
                if (data instanceof TemperatureSensorAvro t) {
                    yield compare(t.getTemperatureC(), expected, operation);
                }
                yield false;
            }
            case HUMIDITY -> {
                if (data instanceof ClimateSensorAvro c) {
                    yield compare(c.getHumidity(), expected, operation);
                }
                yield false;
            }
            case CO2LEVEL -> {
                if (data instanceof ClimateSensorAvro c) {
                    yield compare(c.getCo2Level(), expected, operation);
                }
                yield false;
            }
            case LUMINOSITY -> {
                if (data instanceof LightSensorAvro light) {
                    yield compare(light.getLuminosity(), expected, operation);
                }
                yield false;
            }
            case MOTION -> evaluateMotion(condition, data, expected, operation);
            case SWITCH -> evaluateSwitch(condition, data, expected, operation);
        };
    }

    private boolean evaluateMotion(Condition ignored, Object data, int expected, ConditionOperationAvro operation) {
        if (!(data instanceof MotionSensorAvro motion)) {
            return false;
        }
        int motionVal = motion.getMotion() ? 1 : 0;
        return compare(motionVal, expected, operation);
    }

    private boolean evaluateSwitch(Condition ignored, Object data, int expected, ConditionOperationAvro operation) {
        if (!(data instanceof SwitchSensorAvro sw)) {
            return false;
        }
        int stateVal = sw.getState() ? 1 : 0;
        return compare(stateVal, expected, operation);
    }

    private boolean compare(int sensorValue, int expected, ConditionOperationAvro operation) {
        return switch (operation) {
            case ConditionOperationAvro.GREATER_THAN -> sensorValue > expected;
            case ConditionOperationAvro.LOWER_THAN -> sensorValue < expected;
            case ConditionOperationAvro.EQUALS -> sensorValue == expected;
        };
    }

    private void executeScenarioActions(String hubId, Scenario scenario) {
        Instant now = Instant.now();

        for (ScenarioAction sa : scenario.getActions()) {
            Action action = sa.getAction();
            String sensorId = sa.getSensor().getId();

            Integer rawValue = action.getValue();

            DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                    .setSensorId(sensorId)
                    .setType(mapToProto(action.getType()));
            if (rawValue != null) {
                actionBuilder.setValue(rawValue);
            }
            DeviceActionProto grpcAction = actionBuilder.build();

            DeviceActionRequest request = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenario.getName())
                    .setAction(grpcAction)
                    .setTimestamp(Timestamp.newBuilder()
                            .setSeconds(now.getEpochSecond())
                            .setNanos(now.getNano())
                            .build())
                    .build();

            try {
                hubRouterClient.handleDeviceAction(request);
                log.info("Выполнено действие {} для сенсора {} (hubId={})",
                        action.getType(), sensorId, hubId);
            } catch (StatusRuntimeException e) {
                log.error("Ошибка при вызове gRPC HubRouter: {}", e.getStatus(), e);
            }
        }
    }

    private ActionTypeProto mapToProto(ActionTypeAvro actionTypeAvro) {
        return switch (actionTypeAvro) {
            case INVERSE -> ActionTypeProto.INVERSE;
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
        };
    }

}
