package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.service.CollectorKafkaProducer;

import java.util.List;

import static ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto.ValueCase.BOOL_VALUE;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler {
    public ScenarioAddedEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    public HubEventAvro mapToAvro(HubEventProto event) {
        ScenarioAddedEventProto payloadProto = event.getScenarioAdded();

        List<ScenarioConditionAvro> conditions = payloadProto.getConditionsList()
                .stream()
                .map(this::mapToCondition)
                .toList();

        List<DeviceActionAvro> actions = payloadProto.getActionsList()
                .stream()
                .map(this::mapToAction)
                .toList();

        return initBuilder(event)
                .setPayload(ScenarioAddedEventAvro.newBuilder()
                        .setName(payloadProto.getName())
                        .setConditions(conditions)
                        .setActions(actions)
                        .build())
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    private ScenarioConditionAvro mapToCondition(ScenarioConditionProto condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                .setValue(mapValue(condition))
                .build();
    }

    private DeviceActionAvro mapToAction(DeviceActionProto action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();
    }

    private Integer mapValue(ScenarioConditionProto condition) {
        return switch (condition.getValueCase()) {
            case INT_VALUE -> condition.getIntValue();
            case BOOL_VALUE -> condition.getBoolValue() ? 1 : 0;
            case VALUE_NOT_SET -> null;
        };
    }
}
