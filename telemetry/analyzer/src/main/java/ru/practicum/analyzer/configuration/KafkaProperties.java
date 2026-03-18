package ru.practicum.analyzer.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private DefaultConfig defaultConfig = new DefaultConfig();
    private Map<String, ConsumerConfig> consumers = new HashMap<>();

    @Data
    public static class DefaultConfig {
        private String groupId;
        private Boolean enableAutoCommit;
        private String autoOffsetReset;
        private String keyDeserializer;
    }

    @Data
    public static class ConsumerConfig {
        private List<String> topics;
        private Integer maxPollRecords;
        private Integer pollTimeoutMs;
        private String valueDeserializer;
    }
}
