package ru.practicum.shopping.cart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ChangeQuantityBody(
        @NotBlank
        String productId,

        @NotNull @PositiveOrZero
        Integer newQuantity
) {
}
