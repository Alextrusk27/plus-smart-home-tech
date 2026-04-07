package ru.practicum.shopping.cart.service;

import ru.practicum.interaction.api.dto.request.AddToCartRequest;
import ru.practicum.interaction.api.dto.request.ChangeQuantityRequest;
import ru.practicum.interaction.api.dto.request.RemoveFromCartRequest;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;

public interface ShoppingCartService {

    ShoppingCartDto getCart(String username);

    ShoppingCartDto addToCart(AddToCartRequest request);

    void deactivateCart(String username);

    ShoppingCartDto removeFromCart(RemoveFromCartRequest request);

    ShoppingCartDto changeQuantity(ChangeQuantityRequest request);
}
