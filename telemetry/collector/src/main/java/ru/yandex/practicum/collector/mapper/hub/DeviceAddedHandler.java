package ru.yandex.practicum.collector.mapper.hub;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component
public class DeviceAddedHandler extends BaseHubEventHandler {

    @Override
    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    protected SpecificRecord mapPayload(HubEventProto proto) {

        DeviceAddedEventProto event = proto.getDeviceAdded();

        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(mapDeviceType(event.getType()))
                .build();
    }

    private DeviceTypeAvro mapDeviceType(DeviceTypeProto type) {
        return DeviceTypeAvro.valueOf(type.name());
    }
}
