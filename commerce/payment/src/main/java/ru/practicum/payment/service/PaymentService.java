package ru.practicum.payment.service;

import ru.practicum.interaction.api.dto.response.OrderDto;
import ru.practicum.interaction.api.dto.response.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto payment(OrderDto order);

    BigDecimal productCost(OrderDto order);

    BigDecimal getTotalCost(OrderDto order);

    void paymentSuccess(UUID orderId);

    void paymentFailed(UUID orderId);
}
