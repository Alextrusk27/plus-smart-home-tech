package ru.yandex.practicum.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorKafkaProducer {
    private final Producer<String, byte[]> producer;

    public void send(String topic, Instant timestamp, String key, byte[] message) {
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                topic,
                null,
                timestamp.getEpochSecond(),
                key,
                message
        );
        producer.send(record);
    }
}
