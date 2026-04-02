package ru.yandex.practicum.collector.mapper.hub;

import org.apache.avro.specific.SpecificRecord;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

public abstract class BaseHubEventHandler implements HubEventHandler {

    @Override
    public HubEventAvro handle(HubEventProto proto) {

        SpecificRecord payload = mapPayload(proto);

        return HubEventAvro.newBuilder()
                .setHubId(proto.getHubId())
                .setTimestamp(toInstant(proto.getTimestamp()))
                .setPayload(payload)
                .build();
    }

    protected abstract SpecificRecord mapPayload(HubEventProto proto);

    protected Instant toInstant(com.google.protobuf.Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
