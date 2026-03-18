package ru.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "conditions")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Condition {

    public enum Type {
        MOTION,
        LUMINOSITY,
        SWITCH,
        TEMPERATURE,
        CO2LEVEL,
        HUMIDITY
    }

    public enum Operation {
        EQUALS,
        GREATER_THAN,
        LOWER_THAN;

        public boolean evaluate(int sensorValue, int conditionValue) {
            return switch (this) {
                case EQUALS -> sensorValue == conditionValue;
                case GREATER_THAN -> sensorValue > conditionValue;
                case LOWER_THAN -> sensorValue < conditionValue;
            };
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "operation", nullable = false)
    @Enumerated(EnumType.STRING)
    private Operation operation;

    @Column(name = "value")
    private Integer value;
}
