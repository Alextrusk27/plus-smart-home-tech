package ru.practicum.analyzer.service.snapshot;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.controller.HubRouterClient;
import ru.practicum.analyzer.model.Action;
import ru.practicum.analyzer.model.Condition;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SnapshotService {
    private final HubRouterClient hubRouterClient;
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;

    public void handleSnapshot(SensorsSnapshotAvro snapshot) {
        try {
            String hubId = snapshot.getHubId();
            List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

            if (scenarios.isEmpty()) {
                log.debug("Snapshot handled: no scenarios found for hubId {}", hubId);
                return;
            }

            Map<String, SensorStateAvro> states = snapshot.getSensorsState();
            Timestamp timestamp = Timestamps.fromMillis(System.currentTimeMillis());

            List<DeviceActionRequest> requests = scenarios.stream()
                    .filter(scenario -> isSatisfied(scenario, states))
                    .flatMap(scenario -> scenario.getScenarioActions()
                            .entrySet()
                            .stream()
                            .map(entry -> {
                                DeviceActionProto actionProto = createDeviceAction(entry);
                                return createRequest(scenario, actionProto, timestamp);
                            }))
                    .toList();

            if (!requests.isEmpty()) {
                requests.forEach(hubRouterClient::send);
                log.debug("Snapshot handled: sent {} requests for hubId {}", requests.size(), hubId);
            }
        } catch (Exception e) {
            log.error("Snapshot handled with error ", e);
            throw e;
        }
    }

    private boolean isSatisfied(Scenario scenario, Map<String, SensorStateAvro> states) {
        if (!sensorRepository.existsByIdInAndHubId(states.keySet(), scenario.getHubId())) {
            log.error("Snapshot handled: sensors not registered for hubId {}", scenario.getHubId());
            throw new IllegalArgumentException("Sensors set '%s' not registered for hubId '%s'"
                    .formatted(states.keySet(), scenario.getHubId()));
        }

        return scenario.getScenarioConditions()
                .entrySet()
                .stream()
                .allMatch(entry -> {
                    SensorStateAvro state = states.get(entry.getKey());
                    return checkCondition(state, entry.getValue());
                });
    }

    private boolean checkCondition(SensorStateAvro state, Condition condition) {
        if (state == null || state.getData() == null) {
            log.warn("Sensor state or data is null");
            return false;
        }

        return switch (state.getData()) {
            case ClimateSensorAvro sensor -> evaluateClimateSensor(sensor, condition);
            case LightSensorAvro sensor -> evaluateLightSensor(sensor, condition);
            case MotionSensorAvro sensor -> evaluateMotionSensor(sensor, condition);
            case SwitchSensorAvro sensor -> evaluateSwitchSensor(sensor, condition);
            case TemperatureSensorAvro sensor -> evaluateTemperatureSensor(sensor, condition);
            default -> {
                log.error("Unsupported sensor type: {}", state.getData().getClass().getSimpleName());
                yield false;
            }
        };
    }

    private DeviceActionRequest createRequest(Scenario scenario, DeviceActionProto deviceAction, Timestamp timestamp) {
        return DeviceActionRequest.newBuilder()
                .setHubId(scenario.getHubId())
                .setScenarioName(scenario.getName())
                .setAction(deviceAction)
                .setTimestamp(timestamp)
                .build();
    }

    private DeviceActionProto createDeviceAction(Map.Entry<String, Action> actionAvro) {
        return DeviceActionProto.newBuilder()
                .setSensorId(actionAvro.getKey())
                .setType(ActionTypeProto.valueOf(
                        actionAvro.getValue()
                                .getType()
                                .toString()))
                .setValue(actionAvro.getValue()
                        .getValue())
                .build();
    }

    private boolean evaluateClimateSensor(ClimateSensorAvro climateSensor, Condition condition) {
        int value = switch (condition.getType()) {
            case TEMPERATURE -> climateSensor.getTemperatureC();
            case HUMIDITY -> climateSensor.getHumidity();
            case CO2LEVEL -> climateSensor.getCo2Level();
            default -> {
                log.error("Condition type {} not supported in climate sensor", condition.getType());
                throw new IllegalArgumentException(
                        "Climate sensor cannot check " + condition.getType());
            }
        };
        return condition.getOperation().evaluate(
                value,
                condition.getValue());
    }

    private boolean evaluateLightSensor(LightSensorAvro lightSensor, Condition condition) {
        return condition.getOperation().evaluate(
                lightSensor.getLuminosity(),
                condition.getValue());
    }

    private boolean evaluateMotionSensor(MotionSensorAvro motionSensor, Condition condition) {
        int value = motionSensor.getMotion() ? 1 : 0;
        return condition.getOperation().evaluate(
                value,
                condition.getValue()
        );
    }

    private boolean evaluateSwitchSensor(SwitchSensorAvro switchSensor, Condition condition) {
        int value = switchSensor.getState() ? 1 : 0;
        return condition.getOperation().evaluate(
                value,
                condition.getValue()
        );
    }

    private boolean evaluateTemperatureSensor(TemperatureSensorAvro temperatureSensor, Condition condition) {
        return condition.getOperation().evaluate(
                temperatureSensor.getTemperatureC(),
                condition.getValue()
        );
    }
}
