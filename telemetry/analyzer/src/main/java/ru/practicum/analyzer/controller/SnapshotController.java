package ru.practicum.analyzer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.service.snapshot.SnapshotService;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Service
@RequiredArgsConstructor
public class SnapshotController {
    private final SnapshotService snapshotService;

    public void processRecord(SensorsSnapshotAvro snapshot) {
        snapshotService.handleSnapshot(snapshot);
    }
}
