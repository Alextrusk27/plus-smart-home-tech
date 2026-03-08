package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.service.CollectorKafkaProducer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;

import static ru.yandex.practicum.utils.KafkaTopics.SENSORS_EVENTS;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorEventHandler implements SensorEventHandler {
    protected final CollectorKafkaProducer producer;

    @Override
    public void handle(SensorEventProto event) {
        if (!event.getPayloadCase().equals(getMessageType())) {
            throw new IllegalArgumentException("Unknown event type: " + event.getPayloadCase());
        }

        SensorEventAvro eventAvro = mapToAvro(event);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            DatumWriter<SensorEventAvro> writer = new SpecificDatumWriter<>(SensorEventAvro.class);

            writer.write(eventAvro, encoder);
            encoder.flush();

            byte[] bytes = out.toByteArray();

            producer.send(SENSORS_EVENTS,
                    eventAvro.getTimestamp(),
                    eventAvro.getHubId(),
                    bytes);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Avro serialization error: " + e);
        }
    }

    protected abstract SensorEventAvro mapToAvro(SensorEventProto event);

    protected SensorEventAvro.Builder initBuilder(SensorEventProto event) {
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(
                        event.getTimestamp().getSeconds(),
                        event.getTimestamp().getNanos()
                ));
    }
}
