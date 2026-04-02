package ru.yandex.practicum.collector.mapper.sensor;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class TemperatureSensorHandler extends BaseSensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    protected SpecificRecord mapPayload(SensorEventProto proto) {
        return TemperatureSensorAvro.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(toInstant(proto.getTimestamp()))
                .setTemperatureC(proto.getTemperatureSensor().getTemperatureC())
                .setTemperatureF(proto.getTemperatureSensor().getTemperatureF())
                .build();
    }
}
