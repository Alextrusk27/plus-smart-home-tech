package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.service.CollectorKafkaProducer;

@Component
public class DeviceRemovedEventHandler extends BaseHubEventHandler {
    public DeviceRemovedEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    HubEventAvro mapToAvro(HubEventProto event) {
        DeviceRemovedEventProto payloadProto = event.getDeviceRemoved();

        return initBuilder(event)
                .setPayload(DeviceRemovedEventAvro.newBuilder()
                        .setId(payloadProto.getId())
                        .build())
                .build();
    }
}
