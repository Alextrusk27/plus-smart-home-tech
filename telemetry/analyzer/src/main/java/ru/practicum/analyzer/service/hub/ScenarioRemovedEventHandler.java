package ru.practicum.analyzer.service.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.model.ScenarioAction;
import ru.practicum.analyzer.model.ScenarioCondition;
import ru.practicum.analyzer.repository.*;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {
    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;

    @Override
    @Transactional
    protected void process(String hubId, ScenarioRemovedEventAvro payload) {
        log.info("Processing scenario removed event for hub: {}, scenario: {}",
                hubId, payload.getName());

        String scenarioName = payload.getName();
        Optional<Scenario> scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName);

        scenario.ifPresent(s -> {
            Long scenarioId = s.getId();
            removeScenarioActions(scenarioId);
            removeScenarioConditions(scenarioId);
            scenarioRepository.deleteById(scenarioId);
            log.debug("Successfully removed scenario: {} (id={})", payload.getName(), scenarioId);
        });
    }

    @Override
    public Class<ScenarioRemovedEventAvro> getPayloadClass() {
        return ScenarioRemovedEventAvro.class;
    }

    private void removeScenarioActions(Long scenarioId) {
        List<ScenarioAction> scenarioActions = scenarioActionRepository.findByScenarioId(scenarioId);

        if (scenarioActions.isEmpty()) {
            return;
        }

        List<Long> actionIds = scenarioActions.stream()
                .map(scenarioAction -> scenarioAction.getId().getActionId())
                .distinct()
                .toList();

        scenarioActionRepository.deleteAllInBatch(scenarioActions);
        log.debug("Deleted {} scenario-action links", scenarioActions.size());

        removeActions(actionIds);
    }

    private void removeScenarioConditions(Long scenarioId) {
        List<ScenarioCondition> scenarioConditions = scenarioConditionRepository.findByScenarioId(scenarioId);

        if (scenarioConditions.isEmpty()) {
            return;
        }

        List<Long> conditionIds = scenarioConditions.stream()
                .map(scenarioCondition -> scenarioCondition.getId().getConditionId())
                .distinct()
                .toList();

        scenarioConditionRepository.deleteAllInBatch(scenarioConditions);
        log.debug("Deleted {} scenario-condition links", scenarioConditions.size());

        removeConditions(conditionIds);
    }

    private void removeActions(List<Long> actionIds) {
        List<Long> usedActionIds = scenarioActionRepository.findUsedActionIds(actionIds);

        List<Long> actionsIdsToDelete = actionIds.stream()
                .filter(actionId -> !usedActionIds.contains(actionId))
                .toList();

        actionRepository.deleteAllByIdInBatch(actionsIdsToDelete);
        log.debug("Deleted {} actions", actionsIdsToDelete.size());
    }

    private void removeConditions(List<Long> conditionIds) {
        List<Long> usedConditionIds = scenarioConditionRepository.findUsedConditionIds(conditionIds);

        List<Long> conditionIdsToDelete = conditionIds.stream()
                .filter(id -> !usedConditionIds.contains(id))
                .toList();

        conditionRepository.deleteAllByIdInBatch(conditionIdsToDelete);
        log.debug("Deleted {} conditions", conditionIdsToDelete.size());
    }
}
