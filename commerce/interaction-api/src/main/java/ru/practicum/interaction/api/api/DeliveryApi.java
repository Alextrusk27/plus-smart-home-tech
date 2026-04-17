package ru.practicum.interaction.api.api;

import ru.practicum.interaction.api.dto.request.DeliveryRequest;
import ru.practicum.interaction.api.dto.response.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryApi {
    UUID planDelivery(DeliveryRequest request);

    BigDecimal deliveryCost(OrderDto order);

    void deliveryPicked(UUID orderId);

    void deliverySuccessful(UUID orderId);

    void deliveryFailed(UUID orderId);
}
