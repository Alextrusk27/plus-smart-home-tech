package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.service.CollectorKafkaProducer;

@Component
public class MotionSensorEventHandler extends BaseSensorEventHandler {
    public MotionSensorEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected SensorEventAvro mapToAvro(SensorEventProto event) {
        MotionSensorProto payloadProto = event.getMotionSensor();

        return initBuilder(event)
                .setPayload(MotionSensorAvro.newBuilder()
                        .setLinkQuality(payloadProto.getLinkQuality())
                        .setMotion(payloadProto.getMotion())
                        .setVoltage(payloadProto.getVoltage())
                        .build())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }
}
