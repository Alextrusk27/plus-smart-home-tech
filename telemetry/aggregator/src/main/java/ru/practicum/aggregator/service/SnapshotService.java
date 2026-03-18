package ru.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnapshotService {
    private final Map<String, SensorsSnapshotAvro> snapshots;

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot = getOrCreateSnapshot(event);
        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        String eventId = event.getId();
        boolean updated = false;

        SensorStateAvro newState = createSensorState(event);

        if (sensorsState.containsKey(eventId)) {
            SensorStateAvro oldState = sensorsState.get(eventId);

            if (oldState.getTimestamp().isBefore(event.getTimestamp()) ||
                    !oldState.getData().equals(event.getPayload())) {
                sensorsState.put(eventId, newState);
                updated = true;
                log.debug("Updated sensor {} in hub {}", eventId, snapshot.getHubId());
            }
        } else {
            sensorsState.put(eventId, newState);
            updated = true;
            log.debug("Added new sensor {} to hub {}", eventId, snapshot.getHubId());
        }

        if (updated) {
            snapshot.setTimestamp(event.getTimestamp());
            snapshots.put(snapshot.getHubId(), snapshot);
        }

        return updated ? Optional.of(snapshot) : Optional.empty();
    }

    private SensorsSnapshotAvro getOrCreateSnapshot(SensorEventAvro event) {
        String hubId = event.getHubId();

        return snapshots.computeIfAbsent(hubId, k ->
                SensorsSnapshotAvro.newBuilder()
                        .setHubId(hubId)
                        .setTimestamp(event.getTimestamp())
                        .setSensorsState(new HashMap<>())
                        .build()
        );
    }

    private SensorStateAvro createSensorState(SensorEventAvro event) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
    }
}
