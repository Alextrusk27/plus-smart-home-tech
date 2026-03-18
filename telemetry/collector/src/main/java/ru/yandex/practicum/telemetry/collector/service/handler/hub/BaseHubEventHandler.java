package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.service.CollectorKafkaProducer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;

import static ru.yandex.practicum.utils.KafkaTopics.HUBS_EVENTS;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler implements HubEventHandler {
    protected final CollectorKafkaProducer producer;

    abstract HubEventAvro mapToAvro(HubEventProto event);

    @Override
    public void handle(HubEventProto event) {
        if (!event.getPayloadCase().equals(getMessageType())) {
            throw new IllegalArgumentException("Unknown event type: " + event.getPayloadCase());
        }

        HubEventAvro eventAvro = mapToAvro(event);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            DatumWriter<HubEventAvro> writer = new SpecificDatumWriter<>(HubEventAvro.class);

            writer.write(eventAvro, encoder);
            encoder.flush();

            byte[] bytes = out.toByteArray();

            producer.send(HUBS_EVENTS,
                    eventAvro.getTimestamp(),
                    eventAvro.getHubId(),
                    bytes);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Avro serialization error: " + e);
        }
    }

    protected HubEventAvro.Builder initBuilder(HubEventProto event) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(
                        event.getTimestamp().getSeconds(),
                        event.getTimestamp().getNanos()
                ));
    }
}
