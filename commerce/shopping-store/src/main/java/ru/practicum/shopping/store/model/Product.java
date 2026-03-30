package ru.practicum.shopping.store.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.interaction.api.ProductCategory;
import ru.practicum.interaction.api.ProductState;
import ru.practicum.interaction.api.QuantityState;

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

    @Column(name = "product_name", nullable = false, unique = true)
    private String productName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image_src", nullable = false)
    private String imageSrc;

    @Column(name = "quantity_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private QuantityState quantityState;

    @Column(name = "product_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductState productState;

    @Column(name = "product_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @PrePersist
    public void generateId() {
        if (productId == null) {
            productId = UUID.randomUUID();
        }
    }
}
