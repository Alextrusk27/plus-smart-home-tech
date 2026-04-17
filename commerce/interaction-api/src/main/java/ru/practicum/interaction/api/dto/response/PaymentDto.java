package ru.practicum.interaction.api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import static ru.practicum.interaction.api.constants.VATConstants.VAT;

public record PaymentDto(
        UUID paymentId,
        BigDecimal totalPayment,
        BigDecimal productsTotal,
        BigDecimal deliveryTotal,
        BigDecimal feeTotal
) {
    public static PaymentDto of(
            UUID paymentId,
            BigDecimal totalPayment,
            BigDecimal productsTotal,
            BigDecimal deliveryTotal
    ) {
        return new PaymentDto(
                paymentId,
                totalPayment,
                productsTotal,
                deliveryTotal,
                productsTotal.multiply(VAT)
        );
    }
}
