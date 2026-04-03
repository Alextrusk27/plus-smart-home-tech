package ru.practicum.interaction.api.dto.request;

import jakarta.validation.constraints.*;
import ru.practicum.interaction.api.enums.ProductCategory;
import ru.practicum.interaction.api.enums.ProductState;
import ru.practicum.interaction.api.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(
        @NotNull(message = "Product ID is required")
        UUID productId,

        @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
        String productName,

        @Size(min = 10, message = "Product description must be at least 10 characters")
        String description,

        @Pattern(
                regexp = "^[a-zA-Z0-9/_-]+$",
                message = "Invalid image path format. Expected: images/product/device_1"
        )
        @Size(max = 500, message = "Image path cannot exceed 500 characters")
        String imageSrc,

        QuantityState quantityState,

        ProductState productState,

        ProductCategory productCategory,

        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @DecimalMax(value = "99999999.99", message = "Price cannot exceed 99999999.99")
        @Digits(integer = 8, fraction = 2, message = "Price must have up to 8 digits and 2 decimal places")
        BigDecimal price
) {
}
