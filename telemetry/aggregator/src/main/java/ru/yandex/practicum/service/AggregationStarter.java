package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final KafkaProducer<String, SensorsSnapshotAvro> producer;
    private final AggregationService aggregationService;

    @Value("${kafka.input-topic}")
    private String inputTopic;

    @Value("${kafka.output-topic}")
    private String outputTopic;

       public void start() {
        try {
            consumer.subscribe(List.of(inputTopic));
            log.info("subscribe on topic {}", inputTopic);

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records =
                        consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    try {
                        Optional<SensorsSnapshotAvro> optionalSnapshot =
                                aggregationService.updateState(record.value());

                        optionalSnapshot.ifPresent(snapshot -> {
                            ProducerRecord<String, SensorsSnapshotAvro> producerRecord =
                                    new ProducerRecord<>(outputTopic, snapshot.getHubId(), snapshot);

                            producer.send(producerRecord, (metadata, exception) -> {
                                if (exception != null) {
                                    log.error("exception Kafka: {}",
                                            exception.getMessage(), exception);
                                } else {
                                    log.info("snapshot sent: topic={}, partition={}, offset={}",
                                            metadata.topic(), metadata.partition(), metadata.offset());
                                }
                            });
                        });
                    } catch (Exception e) {
                        log.error("error in record processing: key={}, value={}",
                                record.key(), record.value(), e);
                    }
                }

                consumer.commitSync();
            }

        } catch (WakeupException ignored) {
            log.info("Stop signal was received");
        } catch (Exception e) {
            log.error("Error during processing of events from sensors", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                consumer.close();
                producer.close();
            }
        }
    }
}