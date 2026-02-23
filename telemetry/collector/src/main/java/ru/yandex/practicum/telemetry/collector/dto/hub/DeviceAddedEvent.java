package ru.yandex.practicum.telemetry.collector.dto.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {
    private String id;
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }

    public enum DeviceType {
        MOTION_SENSOR,
        TEMPERATURE_SENSOR,
        LIGHT_SENSOR,
        CLIMATE_SENSOR,
        SWITCH_SENSOR
    }
}
