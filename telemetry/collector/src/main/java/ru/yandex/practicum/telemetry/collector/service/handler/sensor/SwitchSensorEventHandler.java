package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.telemetry.collector.service.CollectorKafkaProducer;

@Component
public class SwitchSensorEventHandler extends BaseSensorEventHandler {
    public SwitchSensorEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected SensorEventAvro mapToAvro(SensorEventProto event) {
        SwitchSensorProto payloadProto = event.getSwitchSensor();

        return initBuilder(event)
                .setPayload(SwitchSensorAvro.newBuilder()
                        .setState(payloadProto.getState())
                        .build())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }
}
