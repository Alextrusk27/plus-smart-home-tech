package ru.practicum.interaction.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ShippedToDeliveryRequest(
        @NotNull
        UUID orderId,

        @NotNull
        UUID deliveryId
) {
}
