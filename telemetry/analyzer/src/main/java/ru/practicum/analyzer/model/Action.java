package ru.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "actions")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Action {

    public enum Type {
        ACTIVATE,
        DEACTIVATE,
        INVERSE,
        SET_VALUE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "value")
    private Integer value;
}
