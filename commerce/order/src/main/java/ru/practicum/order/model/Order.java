package ru.practicum.order.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.interaction.api.enums.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private UUID orderId;

    private String username;

    private UUID shoppingCartId;

    private UUID paymentId;

    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderState state = OrderState.NEW;

    private BigDecimal deliveryWeight;

    private BigDecimal deliveryVolume;

    private BigDecimal deliveryPrice;

    private Boolean fragile;

    private BigDecimal totalPrice;

    private BigDecimal productPrice;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Integer> products;

    @PrePersist
    public void generateOrderId() {
        if (orderId == null) {
            orderId = UUID.randomUUID();
        }
    }
}
