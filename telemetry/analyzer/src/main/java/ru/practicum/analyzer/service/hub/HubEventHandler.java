package ru.practicum.analyzer.service.hub;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventHandler<T extends SpecificRecordBase> {

    void handle(HubEventAvro event);

    Class<T> getPayloadClass();
}
