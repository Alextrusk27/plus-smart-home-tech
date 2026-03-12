package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.analyzer.model.ScenarioAction;

import java.util.List;

public interface ScenarioActionRepository extends JpaRepository<ScenarioAction, Long> {

    List<ScenarioAction> findByScenarioId(Long scenarioId);

    @Query("SELECT DISTINCT sa.id.actionId FROM ScenarioAction sa WHERE sa.id.actionId IN :actionIds")
    List<Long> findUsedActionIds(List<Long> actionIds);
}
