package ru.yandex.practicum.collector.mapper.sensor;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class MotionSensorHandler extends BaseSensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    protected SpecificRecord mapPayload(SensorEventProto proto) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(proto.getMotionSensor().getLinkQuality())
                .setMotion(proto.getMotionSensor().getMotion())
                .setVoltage(proto.getMotionSensor().getVoltage())
                .build();
    }
}