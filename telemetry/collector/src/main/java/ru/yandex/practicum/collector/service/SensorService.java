package ru.yandex.practicum.collector.service;

import ru.yandex.practicum.collector.kafka.KafkaEventProducer;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.mapper.sensor.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SensorService {

    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;

    private final static String TOPIC = "telemetry.sensors.v1";
    private final KafkaEventProducer producer;

    public SensorService(KafkaEventProducer producer, Set<SensorEventHandler> setSensorEventHandlers){
        this.producer = producer;
        this.sensorEventHandlers = setSensorEventHandlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getType,
                        Function.identity()
                ));
    }

    public void handle(SensorEventProto sensorEventProto) {
        SensorEventHandler handler = sensorEventHandlers.get(sensorEventProto.getPayloadCase());

        if (handler == null){
            throw new IllegalArgumentException(
                    "Unknown payload: " + sensorEventProto.getPayloadCase()
            );
        }

        SensorEventAvro sensorEventAvro = handler.handle(sensorEventProto);

        producer.send(TOPIC, sensorEventAvro.getHubId(), sensorEventAvro);
    }
}
