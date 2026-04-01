package ru.practicum.shopping.cart.dto;

import ru.practicum.interaction.api.exception.ProductCartException;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public record AddToCartRequest(
        String username,
        Map<UUID, Integer> products
) {
    public static AddToCartRequest of(
            String username,
            Map<String, Integer> products
    ) {
        return new AddToCartRequest(
                username,
                products.entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> parseProductId(entry.getKey(), username),
                                entry -> parseQuantity(entry.getKey(), entry.getValue(), username)
                        )));
    }

    private static UUID parseProductId(String productId, String username) {
        try {
            return UUID.fromString(productId);
        } catch (IllegalArgumentException e) {
            throw new ProductCartException("Add to cart: Invalid product UUID: %s. Username: %s"
                    .formatted(productId, username));
        }
    }

    private static Integer parseQuantity(String productId, Integer quantity, String username) {
        if (quantity == null || quantity < 1) {
            throw new ProductCartException(
                    "Add to cart: Invalid quantity '%d' for product '%s' (user: %s). Quantity must be at least 1."
                            .formatted(quantity, productId, username)
            );
        }
        return quantity;
    }
}
