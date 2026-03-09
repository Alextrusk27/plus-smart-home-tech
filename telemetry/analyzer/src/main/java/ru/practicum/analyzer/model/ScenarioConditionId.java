package ru.practicum.analyzer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ScenarioConditionId implements Serializable {
    @Serial
    private static final long serialVersionUID = -4045397783907174666L;

    @NotNull
    @Column(name = "scenario_id", nullable = false)
    private Long scenarioId;

    @NotNull
    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    @NotNull
    @Column(name = "condition_id", nullable = false)
    private Long conditionId;
}