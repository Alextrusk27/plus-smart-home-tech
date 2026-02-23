package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.dto.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.dto.hub.HubEventType;
import ru.yandex.practicum.telemetry.collector.dto.hub.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.collector.service.CollectorKafkaProducer;

import java.util.List;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;

        List<ScenarioConditionAvro> conditions = scenarioAddedEvent.getConditions()
                .stream()
                .map(this::mapToCondition)
                .toList();

        List<DeviceActionAvro> actions = scenarioAddedEvent.getActions()
                .stream()
                .map(this::mapToAction)
                .toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    private ScenarioConditionAvro mapToCondition(ScenarioAddedEvent.ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                .setValue(mapValue(condition.getValue()))
                .build();
    }

    private DeviceActionAvro mapToAction(ScenarioAddedEvent.DeviceAction action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();
    }

    private Object mapValue(Object value) {
        return switch (value) {
            case null -> null;
            case Integer i -> i;
            case Boolean b -> b;
            case String s -> throw new IllegalArgumentException(
                    "String not allowed in Avro union (expected null, int, or boolean): " + s);
            default -> throw new IllegalArgumentException(
                    "Type not allowed: " + value.getClass().getSimpleName());
        };
    }
}
