package ru.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.configuration.KafkaProperties;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.serialization.avro.SensorEventDeserializer;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final KafkaProducer producer;
    private final KafkaProperties kafkaProperties;
    private final SnapshotService snapshotService;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public void start() {
        Consumer<String, SensorEventAvro> consumer = createConsumer();
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(kafkaProperties.getTopics().getSensorsEvents()));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records =
                        consumer.poll(Duration.ofMillis(kafkaProperties.getConsumer().getPollTimeoutMs()));

                int recordsProcessed = 0;

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    processRecord(record, recordsProcessed, consumer);
                    recordsProcessed++;
                }
                if (recordsProcessed > 0) {
                    consumer.commitAsync();
                }
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Error processing sensor events", e);
        } finally {
            shutdownConsumer(consumer);
        }
    }

    private Consumer<String, SensorEventAvro> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class);

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                kafkaProperties.getConsumer().getAutoOffsetReset());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                kafkaProperties.getConsumer().isEnableAutoCommit());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
                kafkaProperties.getConsumer().getMaxPollRecords());
        return new KafkaConsumer<>(props);
    }

    private void processRecord(ConsumerRecord<String, SensorEventAvro> record,
                               int recordsProcessed,
                               Consumer<String, SensorEventAvro> consumer) {

        SensorEventAvro event = record.value();
        Optional<SensorsSnapshotAvro> snapshotOpt = snapshotService.updateState(event);

        snapshotOpt.ifPresent(snapshot -> {
            producer.send(
                    kafkaProperties.getTopics().getSnapshotsEvents(),
                    snapshot.getTimestamp(),
                    snapshot.getHubId(),
                    snapshot
            );
            log.debug("Sent snapshot for hub: {}", snapshot.getHubId());
        });
        manageOffsets(record, recordsProcessed, consumer);
    }

    private void shutdownConsumer(Consumer<String, SensorEventAvro> consumer) {
        try (consumer) {
            if (!currentOffsets.isEmpty()) {
                consumer.commitSync(currentOffsets);
            }
            producer.flush();
        } catch (Exception e) {
            log.error("Error during final commit", e);
        }
    }

    private void manageOffsets(ConsumerRecord<String, SensorEventAvro> record, int count,
                               Consumer<String, SensorEventAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Offset registration error: {}", offsets, exception);
                }
            });
        }
    }
}
