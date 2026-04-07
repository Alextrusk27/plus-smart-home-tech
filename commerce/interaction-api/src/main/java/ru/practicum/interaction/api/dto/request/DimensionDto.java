package ru.practicum.interaction.api.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DimensionDto(
        @NotNull(message = "Width cannot be null")
        @DecimalMin(value = "0.01", message = "Width must be positive")
        @DecimalMax(value = "9999.99", message = "Width too large")
        @Digits(integer = 4, fraction = 2, message = "Width must have up to 4 digits and 2 decimal places")
        BigDecimal width,

        @NotNull(message = "Height cannot be null")
        @DecimalMin(value = "0.01", message = "Height must be positive")
        @DecimalMax(value = "9999.99", message = "Height too large")
        @Digits(integer = 4, fraction = 2, message = "Height must have up to 4 digits and 2 decimal places")
        BigDecimal height,

        @NotNull(message = "Depth cannot be null")
        @DecimalMin(value = "0.01", message = "Depth must be positive")
        @DecimalMax(value = "9999.99", message = "Depth too large")
        @Digits(integer = 4, fraction = 2, message = "Depth must have up to 4 digits and 2 decimal places")
        BigDecimal depth
) {
}
