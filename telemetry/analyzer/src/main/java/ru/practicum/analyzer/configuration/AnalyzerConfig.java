package ru.practicum.analyzer.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class AnalyzerConfig {
    private final KafkaProperties kafkaProperties;

    @Bean(destroyMethod = "close")
    public Consumer<String, HubEventAvro> hubEventConsumer() {
        return createConsumer("hub", HubEventAvro.class);
    }

    @Bean(destroyMethod = "close")
    public Consumer<String, SensorsSnapshotAvro> snapshotConsumer() {
        return createConsumer("snapshot", SensorsSnapshotAvro.class);
    }

    @SuppressWarnings("unused")
    private <T> KafkaConsumer<String, T> createConsumer(String name, Class<T> clazz) {
        var defaults = kafkaProperties.getDefaultConfig();
        var config = kafkaProperties.getConsumers().get(name);

        if (config == null) {
            throw new IllegalArgumentException("No config for consumer: " + name);
        }

        Properties props = getProperties(defaults, config);

        KafkaConsumer<String, T> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(config.getTopics());
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        return consumer;
    }

    private Properties getProperties(KafkaProperties.DefaultConfig defaults, KafkaProperties.ConsumerConfig config) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, defaults.getGroupId());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, defaults.getEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, defaults.getAutoOffsetReset());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, defaults.getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, config.getValueDeserializer());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, config.getMaxPollRecords());
        return props;
    }
}
