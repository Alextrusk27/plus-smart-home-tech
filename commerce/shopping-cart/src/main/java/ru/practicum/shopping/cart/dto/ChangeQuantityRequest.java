package ru.practicum.shopping.cart.dto;

import ru.practicum.interaction.api.exception.ProductCartException;

import java.util.UUID;

public record ChangeQuantityRequest(
        String username,
        UUID productId,
        Integer newQuantity
) {
    public static ChangeQuantityRequest of(
            String username,
            ChangeQuantityBody body
    ) {
        return new ChangeQuantityRequest(
                username,
                parseProductId(body.productId(), username),
                body.newQuantity()
        );
    }

    private static UUID parseProductId(String productId, String username) {
        try {
            return UUID.fromString(productId);
        } catch (IllegalArgumentException e) {
            throw new ProductCartException("Change products quantity: Invalid product UUID: %s. Username: %s"
                    .formatted(productId, username));
        }
    }
}
