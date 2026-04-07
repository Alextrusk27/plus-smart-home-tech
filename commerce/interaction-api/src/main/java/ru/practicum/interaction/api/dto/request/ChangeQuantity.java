package ru.practicum.interaction.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record ChangeQuantity(
        @NotNull
        UUID productId,

        @NotNull @PositiveOrZero
        Integer newQuantity
) {
}
