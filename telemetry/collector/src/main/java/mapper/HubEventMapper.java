package mapper;

import dto.hub.HubEvent;
import dto.hub.device.DeviceAddedEvent;
import dto.hub.device.DeviceRemovedEvent;
import dto.hub.device.DeviceType;
import dto.hub.scenario.DeviceAction;
import dto.hub.scenario.ScenarioAddedEvent;
import dto.hub.scenario.ScenarioCondition;
import dto.hub.scenario.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;

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
                .setType(ConditionTypeAvro.valueOf(c.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(c.getOperation().name()))
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
                .setType(ActionTypeAvro.valueOf(a.getType().name()))
                .setValue(a.getValue())
                .build();
    }

    private DeviceAddedEventAvro mapDeviceAdded(DeviceAddedEvent e) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(e.getId())
                .setType(mapDeviceType(e.getDeviceType()))
                .build();
    }

    private DeviceRemovedEventAvro mapDeviceRemoved(DeviceRemovedEvent e) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(e.getId())
                .build();
    }

    private Instant safeTimestamp(Instant instant) {
        return instant != null ? instant : Instant.now();
    }

    private DeviceTypeAvro mapDeviceType(DeviceType type) {
        return DeviceTypeAvro.valueOf(type.name());
    }
}
