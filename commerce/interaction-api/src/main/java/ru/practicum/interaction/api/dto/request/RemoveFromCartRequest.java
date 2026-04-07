package ru.practicum.interaction.api.dto.request;

import ru.practicum.interaction.api.exception.ProductCartException;

import java.util.List;
import java.util.UUID;

public record RemoveFromCartRequest(
        String username,
        List<UUID> productsIds
) {
    public static RemoveFromCartRequest of(
            String username,
            List<String> productsIds
    ) {
        if (productsIds == null || productsIds.isEmpty()) {
            throw new ProductCartException("ProductsIds is empty");
        }
        return new RemoveFromCartRequest(
                username,
                productsIds.stream()
                        .map(productId -> parseProductId(productId, username))
                        .toList());
    }

    private static UUID parseProductId(String productId, String username) {
        try {
            return UUID.fromString(productId);
        } catch (IllegalArgumentException e) {
            throw new ProductCartException("Remove from cart: Invalid product UUID: %s. Username: %s"
                    .formatted(productId, username));
        }
    }
}
