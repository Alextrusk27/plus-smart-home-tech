package ru.practicum.interaction.api.dto.response;

import ru.practicum.interaction.api.enums.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record OrderDto(
        UUID orderId,
        UUID shoppingCartId,
        Map<UUID, Integer> products,
        UUID paymentId,
        UUID deliveryId,
        OrderState state,
        BigDecimal deliveryWeight,
        BigDecimal deliveryVolume,
        BigDecimal deliveryPrice,
        Boolean fragile,
        BigDecimal totalPrice,
        BigDecimal productPrice
) {
}
