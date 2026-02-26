package ru.yandex.practicum.telemetry.collector.utils;

public interface KafkaTopics {
    String HUBS_EVENTS = "telemetry.hubs.v1";
    String SENSORS_EVENTS = "telemetry.sensors.v1";
    String SNAPSHOTS_EVENTS = "telemetry.snapshots.v1";
}
