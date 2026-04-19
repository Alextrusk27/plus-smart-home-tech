package ru.practicum.payment.service;

import ru.practicum.interaction.api.dto.response.OrderDto;
import ru.practicum.interaction.api.dto.response.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto payment(OrderDto order);

    BigDecimal calculateProductCost(OrderDto order);

    BigDecimal calculateTotalCost(OrderDto order);

    void paymentSuccess(UUID orderId);

    void paymentFailed(UUID orderId);
}
