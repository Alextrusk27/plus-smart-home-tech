package ru.practicum.analyzer.service.hub;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Slf4j
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler<T> {

    @Override
    public void handle(HubEventAvro event) {
        String hubId = event.getHubId();
        String eventType = event.getPayload().getClass().getSimpleName();

        try {
            T payload = getPayload(event);

            process(hubId, payload);
        } catch (Exception e) {
            log.error("Failed to process {} event for hub: {}. Event details: {}",
                    eventType, hubId, event, e);

            throw new RuntimeException(String.format(
                    "Failed to process %s event for hub: %s", eventType, hubId), e);
        }
    }

    @SuppressWarnings("unchecked")
    protected T getPayload(HubEventAvro event) {
        return (T) event.getPayload();
    }

    protected abstract void process(String hubId, T payload);
}
