package ru.yandex.practicum.collector.service;

import ru.yandex.practicum.collector.kafka.producer.KafkaEventProducer;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.mapper.hub.HubEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class HubService {
    private final static String TOPIC = "telemetry.hubs.v1";
    private final KafkaEventProducer producer;

    private final Map<HubEventProto.PayloadCase, HubEventHandler> handlers;

    public HubService(Set<HubEventHandler> handlerSet,
                      KafkaEventProducer producer) {

        this.producer = producer;

        this.handlers = handlerSet.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getType,
                        Function.identity()
                ));
    }

    public void handle(HubEventProto hubEvent) {
        HubEventHandler handler = handlers.get(hubEvent.getPayloadCase());

        if (handler == null) {
            throw new IllegalArgumentException(
                    "Unknown payload: " + hubEvent.getPayloadCase()
            );
        }

        HubEventAvro hubEventAvro = handler.handle(hubEvent);

        producer.send(TOPIC, hubEvent.getHubId(), hubEventAvro);
    }
}
