package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.collector.service.CollectorKafkaProducer;

@Component
public class ScenarioRemovedEventHandler extends BaseHubEventHandler {
    public ScenarioRemovedEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    public HubEventAvro mapToAvro(HubEventProto event) {
        ScenarioRemovedEventProto payloadProto = event.getScenarioRemoved();

        return initBuilder(event)
                .setPayload(ScenarioRemovedEventAvro.newBuilder()
                        .setName(payloadProto.getName())
                        .build())
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }
}
