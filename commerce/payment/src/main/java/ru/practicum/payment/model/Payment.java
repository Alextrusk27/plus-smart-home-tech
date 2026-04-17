package ru.practicum.payment.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.interaction.api.enums.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    private UUID paymentId;

    private BigDecimal totalPayment;

    private BigDecimal productsTotal;

    private BigDecimal deliveryTotal;

    @Enumerated(EnumType.STRING)
    private PaymentState state;

    @PrePersist
    public void generateId() {
        if (paymentId == null) {
            paymentId = UUID.randomUUID();
        }
    }
}
