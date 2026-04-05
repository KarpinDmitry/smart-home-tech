package ru.yandex.practicum.collector.kafka;


import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import serializer.KafkaAvroSerializer;

import java.util.Properties;

@Slf4j
@Component
public class KafkaEventProducer {
    private final Producer<String, SpecificRecord> producer;

    public KafkaEventProducer(@Value("${kafka.bootstrap-servers}") String bootstrapServer) {
        Properties config = new Properties();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        this.producer = new KafkaProducer<>(config);
    }

    public void send(String topic, String key, SpecificRecord value) {
        producer.send(new ProducerRecord<>(topic, key, value));
    }

    @PreDestroy
    public void close() {
        producer.close();
    }
}
