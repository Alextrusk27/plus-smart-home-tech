package ru.practicum.analyzer.service.hub;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.mapper.SensorMapper;
import ru.practicum.analyzer.model.Sensor;
import ru.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {
    private final SensorMapper sensorMapper;
    private final SensorRepository sensorRepository;

    @Override
    @Transactional
    protected void process(String hubId, DeviceAddedEventAvro payload) {
        log.info("Processing device added event for hub: {}, deviceId: {}, deviceType: {}",
                hubId, payload.getId(), payload.getType());

        Sensor sensor = sensorMapper.toEntity(hubId, payload.getId());
        sensorRepository.save(sensor);

        log.debug("Successfully added to hub: {} deviceId: {}, deviceType: {}",
                hubId, payload.getId(), payload.getType());
    }

    @Override
    public Class<DeviceAddedEventAvro> getPayloadClass() {
        return DeviceAddedEventAvro.class;
    }
}

