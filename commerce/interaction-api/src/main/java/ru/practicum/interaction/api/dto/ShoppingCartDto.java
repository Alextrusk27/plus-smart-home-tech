package ru.practicum.interaction.api.dto;

import java.util.Map;
import java.util.UUID;

public record ShoppingCartDto(
        UUID shoppingCartId,
        Map<UUID, Integer> products
) {
}
