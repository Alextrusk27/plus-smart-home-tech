package ru.practicum.delivery.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.interaction.api.enums.DeliveryState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "delivery")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {
    @Id
    @Column(name = "id")
    private UUID deliveryId;

    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DeliveryState state = DeliveryState.CREATED;

    private Boolean fragile;

    @Column(name = "weight")
    private BigDecimal deliveryWeight;

    @Column(name = "volume")
    private BigDecimal deliveryVolume;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sender_address_id")
    private Address fromAddress;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "recipient_address_id")
    private Address toAddress;

    @PrePersist
    public void generateId() {
        if (deliveryId == null) {
            deliveryId = UUID.randomUUID();
        }
    }
}
