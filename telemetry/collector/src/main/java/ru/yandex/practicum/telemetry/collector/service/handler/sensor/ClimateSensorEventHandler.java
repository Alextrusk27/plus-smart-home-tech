package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.service.CollectorKafkaProducer;

@Component
public class ClimateSensorEventHandler extends BaseSensorEventHandler {
    public ClimateSensorEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected SensorEventAvro mapToAvro(SensorEventProto event) {
        ClimateSensorProto payloadProto = event.getClimateSensor();

        return initBuilder(event)
                .setPayload(ClimateSensorAvro.newBuilder()
                        .setTemperatureC(payloadProto.getTemperatureC())
                        .setHumidity(payloadProto.getHumidity())
                        .setCo2Level(payloadProto.getCo2Level())
                        .build())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }
}

