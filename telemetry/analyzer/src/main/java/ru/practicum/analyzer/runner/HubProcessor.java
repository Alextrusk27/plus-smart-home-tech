package ru.practicum.analyzer.runner;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.configuration.KafkaProperties;
import ru.practicum.analyzer.controller.HubEventController;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class HubProcessor implements Runnable {
    private final Consumer<String, HubEventAvro> hubEventConsumer;
    private final HubEventController hubEventController;
    private final KafkaProperties kafkaProperties;
    private volatile boolean running = true;

    @Override
    public void run() {
        try {
            while (running) {
                ConsumerRecords<String, HubEventAvro> records = hubEventConsumer.poll(Duration.ofMillis(
                        kafkaProperties.getConsumers().get("hub").getPollTimeoutMs()));

                int recordsProcessed = 0;

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    hubEventController.processRecord(record.value());
                    recordsProcessed++;
                }
                if (recordsProcessed > 0) {
                    hubEventConsumer.commitAsync();
                }
            }
        } catch (WakeupException ignored) {
            log.debug("HubProcessor received wakeup signal");
        } catch (Exception e) {
            log.error("Error processing hub events", e);
        } finally {
            try {
                log.debug("Performing final commit before shutdown");
                hubEventConsumer.commitSync();
            } catch (Exception e) {
                log.error("Error during final commit", e);
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        log.debug("Shutting down HubProcessor");
        running = false;
        hubEventConsumer.wakeup();
    }
}
