package ru.practicum.shopping.cart.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "carts")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    private UUID shoppingCartId;

    private String username;

    @Builder.Default
    private Boolean isActive = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "cart_products",
            joinColumns = @JoinColumn(name = "shopping_cart_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Integer> products;

    @PrePersist
    public void generateId() {
        if (shoppingCartId == null) {
            shoppingCartId = UUID.randomUUID();
        }
    }
}
