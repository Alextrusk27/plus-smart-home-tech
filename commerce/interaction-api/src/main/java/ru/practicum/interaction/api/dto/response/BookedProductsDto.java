package ru.practicum.interaction.api.dto.response;

import java.math.BigDecimal;

public record BookedProductsDto(
        BigDecimal deliveryWeight,
        BigDecimal deliveryVolume,
        Boolean fragile
) {
}
