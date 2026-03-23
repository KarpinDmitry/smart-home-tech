package service;

import dto.hub.HubEvent;
import kafka.producer.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import mapper.HubEventMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Service
@RequiredArgsConstructor
public class HubService {
    private final static String TOPIC = "telemetry.hubs.v1";

    private final HubEventMapper mapper;
    private final KafkaEventProducer producer;

    public void handle(HubEvent hubEvent) {
        HubEventAvro hubEventAvro = mapper.map(hubEvent);

        producer.send(TOPIC, hubEvent.getHubId(), hubEventAvro);
    }
}
