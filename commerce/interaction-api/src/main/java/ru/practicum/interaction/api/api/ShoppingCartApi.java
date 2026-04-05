package ru.practicum.interaction.api.api;

import ru.practicum.interaction.api.dto.request.ChangeQuantity;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;

import java.util.List;
import java.util.Map;

public interface ShoppingCartApi {
    ShoppingCartDto getCart(String username);

    ShoppingCartDto addToCart(String username, Map<String, Integer> products);

    void deactivateCart(String username);

    ShoppingCartDto removeFromCart(String username, List<String> productsIds);

    ShoppingCartDto changeQuantity(String username, ChangeQuantity changeQuantity);
}
