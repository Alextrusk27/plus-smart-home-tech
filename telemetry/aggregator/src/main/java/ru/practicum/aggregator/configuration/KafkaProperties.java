package ru.practicum.aggregator.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private Consumer consumer = new Consumer();
    private Producer producer = new Producer();
    private Topics topics = new Topics();

    @Data
    public static class Consumer {
        private String groupId;
        private String autoOffsetReset;
        private boolean enableAutoCommit;
        private int maxPollRecords;
        private int pollTimeoutMs;
    }

    @Data
    public static class Producer {
        private int retries;
        private int deliveryTimeoutMs;
        private int requestTimeoutMs;
    }

    @Data
    public static class Topics {
        private String sensorsEvents;
        private String snapshotsEvents;
    }
}