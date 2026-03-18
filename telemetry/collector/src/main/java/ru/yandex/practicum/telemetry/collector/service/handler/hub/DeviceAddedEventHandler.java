package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.service.CollectorKafkaProducer;

@Component
public class DeviceAddedEventHandler extends BaseHubEventHandler {
    public DeviceAddedEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    public HubEventAvro mapToAvro(HubEventProto event) {
        DeviceAddedEventProto payloadProto = event.getDeviceAdded();

        return initBuilder(event)
                .setPayload(DeviceAddedEventAvro.newBuilder()
                        .setId(payloadProto.getId())
                        .setType(DeviceTypeAvro.valueOf(payloadProto
                                .getType()
                                .name()))
                        .build())
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }
}
