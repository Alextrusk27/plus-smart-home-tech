package ru.practicum.warehouse.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private UUID productId;
    private Boolean fragile;

    @Embedded
    private Dimension dimension;
    private BigDecimal weight;

    @Builder.Default
    private Integer quantity = 0;
}
