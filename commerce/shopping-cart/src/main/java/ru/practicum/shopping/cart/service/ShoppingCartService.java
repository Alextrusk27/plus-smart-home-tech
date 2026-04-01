package ru.practicum.shopping.cart.service;

import ru.practicum.interaction.api.dto.ShoppingCartDto;
import ru.practicum.shopping.cart.dto.AddToCartRequest;
import ru.practicum.shopping.cart.dto.ChangeQuantityRequest;
import ru.practicum.shopping.cart.dto.RemoveFromCartRequest;

public interface ShoppingCartService {

    ShoppingCartDto getCart(String username);

    ShoppingCartDto addToCart(AddToCartRequest request);

    void removeCart(String username);

    ShoppingCartDto removeFromCart(RemoveFromCartRequest request);

    ShoppingCartDto changeQuantity(ChangeQuantityRequest request);
}
