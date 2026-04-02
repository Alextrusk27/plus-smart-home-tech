package ru.practicum.interaction.api.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record NewProductInWarehouseRequest(
        @NotNull(message = "ProductId cannot be null")
        UUID productId,

        @NotNull(message = "Fragile cannot be null")
        Boolean fragile,

        @NotNull(message = "Dimension cannot be null")
        DimensionDto dimension,

        @NotNull(message = "Weight cannot be null")
        @DecimalMin(value = "0.01", message = "Weight must be positive")
        @DecimalMax(value = "9999.99", message = "Weight too large")
        @Digits(integer = 4, fraction = 2, message = "Weight must have up to 4 digits and 2 decimal places")
        BigDecimal weight
) {
}
