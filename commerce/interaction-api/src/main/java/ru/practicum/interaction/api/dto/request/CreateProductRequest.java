package ru.practicum.interaction.api.dto.request;

import jakarta.validation.constraints.*;
import ru.practicum.interaction.api.enums.ProductCategory;
import ru.practicum.interaction.api.enums.ProductState;
import ru.practicum.interaction.api.enums.QuantityState;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "Product name is required")
        @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
        String productName,

        @NotBlank(message = "Product description is required")
        @Size(min = 10, message = "Product description must be at least 10 characters")
        String description,

        @NotBlank(message = "Product image URL is required")
        @Pattern(
                regexp = "^[a-zA-Z0-9/_-]+$",
                message = "Invalid image path format. Expected: images/product/device_1"
        )
        @Size(max = 500, message = "Image path cannot exceed 500 characters")
        String imageSrc,

        @NotNull(message = "Product quantity state is required")
        QuantityState quantityState,

        @NotNull(message = "Product state is required")
        ProductState productState,

        @NotNull(message = "Product category is required")
        ProductCategory productCategory,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @DecimalMax(value = "99999999.99", message = "Price cannot exceed 99999999.99")
        @Digits(integer = 8, fraction = 2, message = "Price must have up to 8 digits and 2 decimal places")
        BigDecimal price
) {
}
