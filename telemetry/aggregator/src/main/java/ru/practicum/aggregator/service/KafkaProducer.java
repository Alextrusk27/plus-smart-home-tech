package ru.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final Producer<String, SpecificRecordBase> producer;

    public void send(String topic, Instant timestamp, String key, SpecificRecordBase message) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                timestamp.toEpochMilli(),
                key,
                message
        );
        producer.send(record);
    }

    public void flush() {
        producer.flush();
    }
}
