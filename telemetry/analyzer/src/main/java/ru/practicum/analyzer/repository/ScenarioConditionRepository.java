package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.analyzer.model.ScenarioCondition;

import java.util.List;

public interface ScenarioConditionRepository extends JpaRepository<ScenarioCondition, Long> {

    List<ScenarioCondition> findByScenarioId(Long scenarioId);

    @Query("SELECT DISTINCT sc.id.conditionId FROM ScenarioCondition sc WHERE sc.id.conditionId IN :conditionIds")
    List<Long> findUsedConditionIds(List<Long> conditionIds);
}
