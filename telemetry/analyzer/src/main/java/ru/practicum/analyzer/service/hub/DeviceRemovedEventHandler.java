package ru.practicum.analyzer.service.hub;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.Sensor;
import ru.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeviceRemovedEventHandler extends BaseHubEventHandler<DeviceRemovedEventAvro> {
    private final SensorRepository sensorRepository;

    @Override
    @Transactional
    protected void process(String hubId, DeviceRemovedEventAvro payload) {
        String deviceId = payload.getId();

        log.debug("Processing device removed event for hub: {}, deviceId: {}",
                hubId, deviceId);

        Optional<Sensor> sensor = sensorRepository.findByIdAndHubId(deviceId, hubId);

        if (sensor.isPresent()) {
            sensorRepository.deleteById(deviceId);
            log.debug("Successfully removed from hub: {} deviceId: {}", hubId, deviceId);
        } else {
            log.warn("Device not found for hub: {}, deviceId: {}", hubId, deviceId);
        }
    }

    @Override
    public Class<DeviceRemovedEventAvro> getPayloadClass() {
        return DeviceRemovedEventAvro.class;
    }
}

