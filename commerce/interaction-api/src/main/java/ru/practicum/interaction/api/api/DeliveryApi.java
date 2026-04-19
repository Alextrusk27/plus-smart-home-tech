package ru.practicum.interaction.api.api;

import ru.practicum.interaction.api.dto.request.DeliveryRequest;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryApi {
    UUID planDelivery(DeliveryRequest request);

    BigDecimal deliveryCost(UUID deliveryId);

    void deliveryPicked(UUID orderId);

    void deliverySuccessful(UUID orderId);

    void deliveryFailed(UUID orderId);
}
