package ru.practicum.analyzer.service.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.repository.ActionRepository;
import ru.practicum.analyzer.repository.ConditionRepository;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {
    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;

    @Override
    @Transactional
    protected void process(String hubId, ScenarioRemovedEventAvro payload) {
        log.debug("Processing scenario removed event for hub: {}, scenario: {}",
                hubId, payload.getName());

        String scenarioName = payload.getName();
        Optional<Scenario> scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName);

        scenario.ifPresent(s -> {
            Long scenarioId = s.getId();
            scenarioRepository.deleteById(scenarioId);

            log.debug("Successfully removed scenario: {} (id={})", payload.getName(), scenarioId);
        });
    }

    @Override
    public Class<ScenarioRemovedEventAvro> getPayloadClass() {
        return ScenarioRemovedEventAvro.class;
    }
}
