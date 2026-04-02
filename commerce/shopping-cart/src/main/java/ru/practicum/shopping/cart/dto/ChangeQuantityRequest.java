package ru.practicum.shopping.cart.dto;

import ru.practicum.interaction.api.dto.request.ChangeQuantity;

import java.util.UUID;

public record ChangeQuantityRequest(
        String username,
        UUID productId,
        Integer newQuantity
) {
    public static ChangeQuantityRequest of(
            String username,
            ChangeQuantity changeQuantity
    ) {
        return new ChangeQuantityRequest(
                username,
                changeQuantity.productId(),
                changeQuantity.newQuantity()
        );
    }
}
