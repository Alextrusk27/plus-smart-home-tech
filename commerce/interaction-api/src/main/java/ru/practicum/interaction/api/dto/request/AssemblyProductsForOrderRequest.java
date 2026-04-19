package ru.practicum.interaction.api.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record AssemblyProductsForOrderRequest(
        @NotNull
        @NotEmpty
        Map<UUID, Integer> products,

        @NotNull
        UUID orderId
) {
}
