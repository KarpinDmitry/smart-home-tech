package ru.yandex.practicum.collector.mapper.hub;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Component
public class DeviceRemovedHandler extends BaseHubEventHandler {

    @Override
    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    protected SpecificRecord mapPayload(HubEventProto proto) {

        return DeviceRemovedEventAvro.newBuilder()
                .setId(proto.getDeviceRemoved().getId())
                .build();
    }
}
