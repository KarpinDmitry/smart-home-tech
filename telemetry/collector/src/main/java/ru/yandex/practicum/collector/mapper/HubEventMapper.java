package ru.yandex.practicum.collector.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.hub.HubEvent;
import ru.yandex.practicum.collector.dto.hub.device.DeviceAddedEvent;
import ru.yandex.practicum.collector.dto.hub.device.DeviceRemovedEvent;
import ru.yandex.practicum.collector.dto.hub.device.DeviceType;
import ru.yandex.practicum.collector.dto.hub.scenario.DeviceAction;
import ru.yandex.practicum.collector.dto.hub.scenario.ScenarioAddedEvent;
import ru.yandex.practicum.collector.dto.hub.scenario.ScenarioCondition;
import ru.yandex.practicum.collector.dto.hub.scenario.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;

@Component
public class HubEventMapper {

    public HubEventAvro map(HubEvent event) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(safeTimestamp(event.getTimestamp()))
                .setPayload(mapPayload(event))
                .build();
    }

    private Object mapPayload(HubEvent event) {
        return switch (event.getType()) {
            case DEVICE_ADDED -> mapDeviceAdded((DeviceAddedEvent) event);
            case DEVICE_REMOVED -> mapDeviceRemoved((DeviceRemovedEvent) event);
            case SCENARIO_ADDED -> mapScenarioAdded((ScenarioAddedEvent) event);
            case SCENARIO_REMOVED -> mapScenarioRemoved((ScenarioRemovedEvent) event);
        };
    }

    private ScenarioRemovedEventAvro mapScenarioRemoved(ScenarioRemovedEvent e) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(e.getName())
                .build();
    }

    private ScenarioAddedEventAvro mapScenarioAdded(ScenarioAddedEvent e) {
        return ScenarioAddedEventAvro.newBuilder()
                .setName(e.getName())
                .setConditions(mapConditions(e.getConditions()))
                .setActions(mapActions(e.getActions()))
                .build();
    }

    private List<ScenarioConditionAvro> mapConditions(List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(this::mapCondition)
                .toList();
    }

    private ScenarioConditionAvro mapCondition(ScenarioCondition c) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(c.getSensorId())
                .setType(c.getType() != null ? ConditionTypeAvro.valueOf(c.getType().name()) : null)
                .setOperation(c.getOperation() != null ? ConditionOperationAvro.valueOf(c.getOperation().name()) : null)
                .setValue(c.getValue())
                .build();
    }

    private List<DeviceActionAvro> mapActions(List<DeviceAction> actions) {
        return actions.stream()
                .map(this::mapAction)
                .toList();
    }

    private DeviceActionAvro mapAction(DeviceAction a) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(a.getSensorId())
                .setType(a.getType() != null ? ActionTypeAvro.valueOf(a.getType().name()) : null)
                .setValue(a.getValue()) // Integer, допускает null
                .build();
    }

    private DeviceAddedEventAvro mapDeviceAdded(DeviceAddedEvent e) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(e.getId())
                .setType(e.getDeviceType() != null ? mapDeviceType(e.getDeviceType()) : null)
                .build();
    }

    private DeviceRemovedEventAvro mapDeviceRemoved(DeviceRemovedEvent e) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(e.getId())
                .build();
    }

    private DeviceTypeAvro mapDeviceType(DeviceType type) {
        return DeviceTypeAvro.valueOf(type.name());
    }

    /** Avro-поле timestamp объявлено как timestamp-millis → Instant. */
    private Instant safeTimestamp(Instant instant) {
        return instant != null ? instant : Instant.now();
    }
}