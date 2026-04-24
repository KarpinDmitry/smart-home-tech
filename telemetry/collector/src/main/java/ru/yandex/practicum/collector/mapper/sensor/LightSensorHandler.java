package ru.yandex.practicum.collector.mapper.sensor;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorHandler extends BaseSensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    protected SpecificRecord mapPayload(SensorEventProto event) {
        return LightSensorAvro.newBuilder()
                .setLuminosity(event.getLightSensor().getLuminosity())
                .setLinkQuality(event.getLightSensor().getLinkQuality())
                .build();
    }
}
