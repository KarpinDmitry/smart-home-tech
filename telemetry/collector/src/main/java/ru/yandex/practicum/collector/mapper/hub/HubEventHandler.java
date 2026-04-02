package ru.yandex.practicum.collector.mapper.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventHandler {

    HubEventProto.PayloadCase getType();

    HubEventAvro handle(HubEventProto proto);
}
