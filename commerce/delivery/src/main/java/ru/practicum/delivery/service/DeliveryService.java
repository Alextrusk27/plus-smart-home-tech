package ru.practicum.delivery.service;

import ru.practicum.interaction.api.dto.request.DeliveryRequest;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    UUID planDelivery(DeliveryRequest request);

    BigDecimal deliveryCost(UUID deliveryId);

    void deliveryPicked(UUID orderId);

    void deliverySuccessful(UUID orderId);

    void deliveryFailed(UUID orderId);

    void deliveryCancelled(UUID orderId);
}