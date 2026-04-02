package ru.practicum.interaction.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record ChangeQuantity(
        @NotBlank
        UUID productId,

        @NotNull @PositiveOrZero
        Integer newQuantity
) {
}
