package ru.practicum.interaction.api.api;

import ru.practicum.interaction.api.dto.response.OrderDto;
import ru.practicum.interaction.api.dto.response.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentApi {

    PaymentDto payment(OrderDto order);

    BigDecimal productCost(OrderDto order);

    BigDecimal totalCost(OrderDto order);

    void paymentSuccess(UUID orderId);

    void paymentFailed(UUID orderId);

    void paymentCancelled(UUID orderId);
}
