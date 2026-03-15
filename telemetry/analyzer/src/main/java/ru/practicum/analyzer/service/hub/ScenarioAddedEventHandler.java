package ru.practicum.analyzer.service.hub;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.mapper.ActionMapper;
import ru.practicum.analyzer.mapper.ConditionMapper;
import ru.practicum.analyzer.mapper.ScenarioMapper;
import ru.practicum.analyzer.model.Action;
import ru.practicum.analyzer.model.Condition;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.repository.ActionRepository;
import ru.practicum.analyzer.repository.ConditionRepository;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    @Transactional
    protected void process(String hubId, ScenarioAddedEventAvro payload) {
        log.info("Processing scenario added event for hub: {}, scenario: {}",
                hubId, payload.getName());

        Scenario scenario = scenarioMapper.toEntity(hubId, payload.getName());

        Map<String, Condition> conditions = getConditions(payload.getConditions());
        Map<String, Action> actions = getActions(payload.getActions());

        scenario.setScenarioConditions(conditions);
        scenario.setScenarioActions(actions);

        actionRepository.saveAll(actions.values());
        conditionRepository.saveAll(conditions.values());

        scenarioRepository.save(scenario);
        log.debug("Scenario for hub: {} added successfully", hubId);
    }

    @Override
    public Class<ScenarioAddedEventAvro> getPayloadClass() {
        return ScenarioAddedEventAvro.class;
    }

    private Map<String, Condition> getConditions(List<ScenarioConditionAvro> conditionsAvro) {
        if (conditionsAvro == null) {
            return Map.of();
        }
        return conditionsAvro.stream()
                .collect(Collectors.toMap(
                        ScenarioConditionAvro::getSensorId,
                        conditionMapper::toEntity
                ));
    }

    private Map<String, Action> getActions(List<DeviceActionAvro> actionsAvro) {
        if (actionsAvro == null) {
            return Map.of();
        }

        return actionsAvro.stream()
                .collect(Collectors.toMap(
                        DeviceActionAvro::getSensorId,
                        actionMapper::toEntity
                ));
    }
}

