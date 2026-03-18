package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.service.CollectorKafkaProducer;

@Component
public class LightSensorEventHandler extends BaseSensorEventHandler {
    public LightSensorEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    protected SensorEventAvro mapToAvro(SensorEventProto event) {
        LightSensorProto payloadProto = event.getLightSensor();

        return initBuilder(event)
                .setPayload(LightSensorAvro.newBuilder()
                        .setLinkQuality(payloadProto.getLinkQuality())
                        .setLuminosity(payloadProto.getLuminosity())
                        .build())
                .build();
    }
}
