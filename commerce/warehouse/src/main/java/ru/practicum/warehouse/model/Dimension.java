package ru.practicum.warehouse.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;

@Embeddable
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Dimension {
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal depth;
}
