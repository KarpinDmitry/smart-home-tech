package ru.yandex.practicum.collector.mapper.hub;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class ScenarioRemovedHandler extends BaseHubEventHandler {

    @Override
    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    protected SpecificRecord mapPayload(HubEventProto proto) {

        return ScenarioRemovedEventAvro.newBuilder()
                .setName(proto.getScenarioRemoved().getName())
                .build();
    }
}
