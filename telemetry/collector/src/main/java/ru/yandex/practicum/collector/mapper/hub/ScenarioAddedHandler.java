package ru.yandex.practicum.collector.mapper.hub;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Component
public class ScenarioAddedHandler extends BaseHubEventHandler {

    @Override
    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    protected SpecificRecord mapPayload(HubEventProto proto) {

        ScenarioAddedEventProto event = proto.getScenarioAdded();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(mapConditions(event.getConditionList()))
                .setActions(mapActions(event.getActionList()))
                .build();
    }

    private List<ScenarioConditionAvro> mapConditions(List<ScenarioConditionProto> list) {
        return list.stream()
                .map(this::mapCondition)
                .toList();
    }

    private ScenarioConditionAvro mapCondition(ScenarioConditionProto proto) {

        ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder()
                .setSensorId(proto.getSensorId())
                .setType(ConditionTypeAvro.valueOf(proto.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(proto.getOperation().name()));

        switch (proto.getValueCase()) {
            case BOOL_VALUE -> builder.setValue(proto.getBoolValue());
            case INT_VALUE -> builder.setValue(proto.getIntValue());
            default -> throw new IllegalArgumentException("Unknown condition value");
        }

        return builder.build();
    }

    private List<DeviceActionAvro> mapActions(List<DeviceActionProto> list) {
        return list.stream()
                .map(this::mapAction)
                .toList();
    }

    private DeviceActionAvro mapAction(DeviceActionProto proto) {

        DeviceActionAvro.Builder builder = DeviceActionAvro.newBuilder()
                .setSensorId(proto.getSensorId())
                .setType(ActionTypeAvro.valueOf(proto.getType().name()));

        if (proto.hasValue()) {
            builder.setValue(proto.getValue());
        }

        return builder.build();
    }
}
