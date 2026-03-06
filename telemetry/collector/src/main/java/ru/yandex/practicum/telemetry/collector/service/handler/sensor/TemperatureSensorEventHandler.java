package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.collector.service.CollectorKafkaProducer;

@Component
public class TemperatureSensorEventHandler extends BaseSensorEventHandler {
    public TemperatureSensorEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected SensorEventAvro mapToAvro(SensorEventProto event) {
        TemperatureSensorProto payloadProto = event.getTemperatureSensor();

        return initBuilder(event)
                .setPayload(TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(payloadProto.getTemperatureC())
                        .setTemperatureF(payloadProto.getTemperatureF())
                        .build())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }
}
