package ru.yandex.practicum.telemetry.collector.dto.hub;

import lombok.*;

import java.util.Collection;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {
    private String name;
    private Collection<ScenarioCondition> conditions;
    private Collection<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScenarioCondition {
        private String sensorId;
        private ConditionType type;
        private ConditionOperation operation;
        private Object value;

        public enum ConditionType {
            MOTION,
            LUMINOSITY,
            SWITCH,
            TEMPERATURE,
            CO2LEVEL,
            HUMIDITY
        }

        public enum ConditionOperation {
            EQUALS,
            GREATER_THAN,
            LOWER_THAN
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceAction {
        private String sensorId;
        private ActionType type;
        private Integer value;

        public enum ActionType {
            ACTIVATE,
            DEACTIVATE,
            INVERSE,
            SET_VALUE
        }
    }
}
