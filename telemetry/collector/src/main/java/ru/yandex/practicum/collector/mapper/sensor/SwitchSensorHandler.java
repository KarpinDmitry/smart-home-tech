package ru.yandex.practicum.collector.mapper.sensor;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorHandler extends BaseSensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }

    @Override
    protected SpecificRecord mapPayload(SensorEventProto proto) {
        return SwitchSensorAvro.newBuilder()
                .setState(proto.getSwitchSensor().getState())
                .build();
    }
}
