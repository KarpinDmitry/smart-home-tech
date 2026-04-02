package ru.yandex.practicum.collector.mapper.sensor;

import org.apache.avro.specific.SpecificRecord;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getType();

    SensorEventAvro handle(SensorEventProto event);
}
