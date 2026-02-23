package ru.yandex.practicum.telemetry.collector.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.telemetry.collector.dto.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.dto.hub.HubEventType;
import ru.yandex.practicum.telemetry.collector.dto.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.collector.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.telemetry.collector.service.handler.sensor.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class CollectorController {
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    public CollectorController(Set<SensorEventHandler> sensorEventHandlers, Set<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.CREATED)
    public void collectSensorEvent(@RequestBody @Valid SensorEvent sensorEvent) {
        log.info("Collecting sensor event {}", sensorEvent);
        SensorEventHandler sensorEventHandler = sensorEventHandlers.get(sensorEvent.getType());
        if (sensorEventHandler == null) {
            throw new IllegalStateException("Sensor event type not found: " + sensorEvent.getType());
        }
        sensorEventHandler.handle(sensorEvent);
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.CREATED)
    public void collectHubEvent(@RequestBody @Valid HubEvent hubEvent) {
        log.info("Collecting hub event {}", hubEvent);
        HubEventHandler hubEventHandler = hubEventHandlers.get(hubEvent.getType());
        if (hubEventHandler == null) {
            throw new IllegalArgumentException("Hub event type not found: " + hubEvent.getType());
        }
        hubEventHandler.handle(hubEvent);
    }
}
