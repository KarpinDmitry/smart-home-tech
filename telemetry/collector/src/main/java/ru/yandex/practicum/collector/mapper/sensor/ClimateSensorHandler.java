package ru.yandex.practicum.collector.mapper.sensor;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Component
public class ClimateSensorHandler extends BaseSensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    protected SpecificRecord mapPayload(SensorEventProto proto) {
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(proto.getClimateSensor().getTemperatureC())
                .setHumidity(proto.getClimateSensor().getHumidity())
                .setCo2Level(proto.getClimateSensor().getCo2Level())
                .build();
    }
}