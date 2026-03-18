package ru.practicum.analyzer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.service.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HubEventController {
    private final Map<Class<?>, HubEventHandler<?>> hubEventHandlers;

    public HubEventController(Set<HubEventHandler<?>> handlers) {
        this.hubEventHandlers = handlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getPayloadClass,
                        Function.identity()
                ));
    }

    public void processRecord(HubEventAvro hubEvent) {
        try {
            Object payload = hubEvent.getPayload();
            Class<?> payloadClass = payload.getClass();

            HubEventHandler<?> handler = hubEventHandlers.get(payloadClass);

            if (handler == null) {
                log.error("No handler found for event type: {}", payloadClass.getSimpleName());
                throw new IllegalArgumentException("Unknown event type: " + payloadClass);
            }
            handler.handle(hubEvent);
            log.info("Hub event for HubId {} handled", hubEvent.getHubId());
        } catch (Exception e) {
            log.error("Error processing hub event", e);
            throw e;
        }
    }
}
