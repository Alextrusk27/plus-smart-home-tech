package ru.practicum.analyzer.service.hub;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.mapper.ActionMapper;
import ru.practicum.analyzer.mapper.ConditionMapper;
import ru.practicum.analyzer.mapper.ScenarioMapper;
import ru.practicum.analyzer.model.*;
import ru.practicum.analyzer.repository.*;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;
import java.util.stream.IntStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    private final ScenarioMapper scenarioMapper;
    private final ActionMapper actionMapper;
    private final ConditionMapper conditionMapper;

    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final SensorRepository sensorRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;


    @Override
    @Transactional
    protected void process(String hubId, ScenarioAddedEventAvro payload) {
        log.info("Processing scenario added event for hub: {}, scenario: {}",
                hubId, payload.getName());

        Scenario scenario = scenarioMapper.toEntity(hubId, payload.getName());

        scenarioRepository.save(scenario);

        log.debug("Saving {} actions for scenario", payload.getActions().size());
        saveScenarioActions(payload, scenario);

        log.debug("Saving {} conditions for scenario", payload.getConditions().size());
        saveScenarioConditions(payload, scenario);

        log.debug("Successfully processed scenario: {} with id: {}",
                payload.getName(), scenario.getId());
    }

    @Override
    public Class<ScenarioAddedEventAvro> getPayloadClass() {
        return ScenarioAddedEventAvro.class;
    }

    private void saveScenarioActions(ScenarioAddedEventAvro payload, Scenario scenario) {
        List<DeviceActionAvro> deviceActions = payload.getActions();

        if (deviceActions.isEmpty()) {
            return;
        }

        List<Action> savedActions = saveActions(deviceActions);

        scenarioActionRepository.saveAll(
                IntStream.range(0, deviceActions.size())
                        .mapToObj(i ->
                                createScenarioAction(scenario,
                                        deviceActions.get(i),
                                        savedActions.get(i)))
                        .toList()
        );
    }

    private void saveScenarioConditions(ScenarioAddedEventAvro payload, Scenario scenario) {
        List<ScenarioConditionAvro> scenarioConditions = payload.getConditions();

        if (scenarioConditions.isEmpty()) {
            return;
        }

        List<Condition> savedConditions = saveConditions(scenarioConditions);

        scenarioConditionRepository.saveAll(
                IntStream.range(0, scenarioConditions.size())
                        .mapToObj(i ->
                                createScenarioCondition(scenario,
                                        scenarioConditions.get(i),
                                        savedConditions.get(i)))
                        .toList()
        );

    }

    private List<Action> saveActions(List<DeviceActionAvro> deviceActions) {
        return actionRepository.saveAll(
                deviceActions.stream()
                        .map(actionMapper::toEntity)
                        .toList()
        );
    }

    private List<Condition> saveConditions(List<ScenarioConditionAvro> scenarioConditions) {
        return conditionRepository.saveAll(
                scenarioConditions.stream()
                        .map(conditionMapper::toEntity)
                        .toList()
        );
    }

    private ScenarioAction createScenarioAction(Scenario scenario, DeviceActionAvro deviceAction, Action action) {
        return ScenarioAction.builder()
                .id(ScenarioActionId.builder()
                        .scenarioId(scenario.getId())
                        .sensorId(deviceAction.getSensorId())
                        .actionId(action.getId())
                        .build())
                .scenario(scenario)
                .sensor(sensorRepository.getReferenceById(deviceAction.getSensorId()))
                .action(action)
                .build();
    }

    private ScenarioCondition createScenarioCondition(Scenario scenario, ScenarioConditionAvro scenarioCondition,
                                                      Condition condition) {
        return ScenarioCondition.builder()
                .id(ScenarioConditionId.builder()
                        .scenarioId(scenario.getId())
                        .sensorId(scenarioCondition.getSensorId())
                        .conditionId(condition.getId())
                        .build())
                .scenario(scenario)
                .sensor(sensorRepository.getReferenceById(scenarioCondition.getSensorId()))
                .condition(condition)
                .build();
    }
}

