package ru.practicum.shopping.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;
import ru.practicum.interaction.api.exception.CartNotFoundException;
import ru.practicum.interaction.api.exception.NoProductsInShoppingCartException;
import ru.practicum.interaction.api.dto.request.AddToCartRequest;
import ru.practicum.interaction.api.dto.request.ChangeQuantityRequest;
import ru.practicum.interaction.api.dto.request.RemoveFromCartRequest;
import ru.practicum.shopping.cart.mapper.ShoppingCartMapper;
import ru.practicum.shopping.cart.model.Cart;
import ru.practicum.shopping.cart.repository.ShoppingCartRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getCart(String username) {
        return shoppingCartRepository.findByUsername(username)
                .map(shoppingCartMapper::toDto)
                .orElseThrow(() -> noCartByUser(username));
    }

    @Override
    public ShoppingCartDto addToCart(AddToCartRequest request) {
        Cart cart = shoppingCartRepository.findByUsername(request.username())
                .orElseGet(() -> shoppingCartMapper.toEntity(request));

        request.products().forEach((id, qty) ->
                cart.getProducts().merge(id, qty, Integer::sum)
        );
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public void removeCart(String username) {
        shoppingCartRepository.delete(
                shoppingCartRepository.findByUsername(username)
                        .orElseThrow(() -> noCartByUser(username))
        );
    }

    @Override
    public ShoppingCartDto removeFromCart(RemoveFromCartRequest request) {
        Cart cart = shoppingCartRepository.findByUsername(request.username())
                .orElseThrow(() -> noCartByUser(request.username()));

        List<UUID> notFound = request.productsIds().stream()
                .filter(id -> !cart.getProducts().containsKey(id))
                .toList();

        if (!notFound.isEmpty()) {
            throw new NoProductsInShoppingCartException("Cart '%s' has no such products: '%s'"
                    .formatted(cart.getShoppingCartId(), notFound));
        }

        request.productsIds().forEach(id -> cart.getProducts().remove(id));
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto changeQuantity(ChangeQuantityRequest request) {
        Cart cart = shoppingCartRepository.findByUsername(request.username())
                .orElseThrow(() -> noCartByUser(request.username()));

        if (!cart.getProducts().containsKey(request.productId())) {
            throw new NoProductsInShoppingCartException("Cart '%s' has no product: '%s'"
                    .formatted(cart.getShoppingCartId(), request.productId()));
        }

        cart.getProducts().put(request.productId(), request.newQuantity());
        return shoppingCartMapper.toDto(cart);
    }

    private CartNotFoundException noCartByUser(String username) {
        return new CartNotFoundException("Cart for user '%s' not found"
                .formatted(username));
    }
}
