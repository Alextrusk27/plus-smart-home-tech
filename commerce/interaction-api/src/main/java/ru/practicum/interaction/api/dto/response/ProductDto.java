package ru.practicum.interaction.api.dto.response;

import ru.practicum.interaction.api.enums.ProductCategory;
import ru.practicum.interaction.api.enums.ProductState;
import ru.practicum.interaction.api.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(
        UUID productId,
        String productName,
        String description,
        String imageSrc,
        QuantityState quantityState,
        ProductState productState,
        ProductCategory productCategory,
        BigDecimal price
) {
}
