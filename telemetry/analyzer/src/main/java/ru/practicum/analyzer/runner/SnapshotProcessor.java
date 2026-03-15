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
import ru.practicum.analyzer.controller.SnapshotController;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {
    private final Consumer<String, SensorsSnapshotAvro> snapshotConsumer;
    private final SnapshotController snapshotController;
    private final KafkaProperties kafkaProperties;
    private volatile boolean running = true;

    @Override
    public void run() {
        try {
            while (running) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = snapshotConsumer.poll(Duration.ofMillis(
                        kafkaProperties.getConsumers().get("snapshot").getPollTimeoutMs()));

                int recordsProcessed = 0;

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    snapshotController.processRecord(record.value());
                    recordsProcessed++;
                }
                if (recordsProcessed > 0) {
                    snapshotConsumer.commitAsync();
                }
            }
        } catch (WakeupException ignored) {
            log.debug("SnapshotProcessor received wakeup signal");
        } catch (Exception e) {
            log.error("Error processing snapshot events", e);
        } finally {
            try {
                log.debug("Performing final commit before shutdown");
                snapshotConsumer.commitSync();
            } catch (Exception e) {
                log.error("Error during final commit", e);
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        log.debug("Shutting down SnapshotProcessor");
        running = false;
        snapshotConsumer.wakeup();
    }
}
