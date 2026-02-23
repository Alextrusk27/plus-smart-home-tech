package ru.yandex.practicum.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectorKafkaProducer {
    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    public void send(String topic, SpecificRecordBase message) {
        CompletableFuture<SendResult<String, SpecificRecordBase>> future = kafkaTemplate.send(topic, message);

        future.whenComplete((result, e) -> {
            if (e == null) {
                log.info("Sent message to topic {}: offset = {}",
                        topic, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message to topic {}: ", topic, e);
            }
        });
    }
}
