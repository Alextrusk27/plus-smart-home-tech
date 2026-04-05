package ru.practicum.shopping.cart.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.interaction.api.dto.request.AddToCartRequest;
import ru.practicum.interaction.api.dto.request.ChangeQuantityRequest;
import ru.practicum.interaction.api.dto.request.RemoveFromCartRequest;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;
import ru.practicum.interaction.api.exception.CartNotFoundException;
import ru.practicum.interaction.api.exception.NoProductsInShoppingCartException;
import ru.practicum.interaction.api.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.practicum.interaction.api.feign.WarehouseClient;
import ru.practicum.shopping.cart.mapper.ShoppingCartMapper;
import ru.practicum.shopping.cart.model.Cart;
import ru.practicum.shopping.cart.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getCart(String username) {
        return shoppingCartRepository.findByUsernameAndIsActiveTrue(username)
                .map(shoppingCartMapper::toDto)
                .orElseThrow(() -> noCartByUser(username));
    }

    @Override
    public ShoppingCartDto addToCart(AddToCartRequest request) {
        Cart cart = shoppingCartRepository.findByUsernameAndIsActiveTrue(request.username())
                .orElseGet(() -> {
                    Cart newCart = shoppingCartMapper.toEntity(request);
                    newCart.setProducts(new HashMap<>());
                    return newCart;
                });

        try {
            warehouseClient.checkProduct(shoppingCartMapper.toDto(shoppingCartMapper.toEntity(request)));
        } catch (FeignException e) {
            throw new ProductInShoppingCartLowQuantityInWarehouse(
                    "Cannot add product to shopping cart, due to low quantity in warehouse: %s"
                            .formatted(e.getMessage()));
        }

        request.products().forEach((id, qty) ->
                cart.getProducts().merge(id, qty, Integer::sum)
        );

        shoppingCartRepository.save(cart);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public void deactivateCart(String username) {
        Cart cart = shoppingCartRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> noCartByUser(username));
        cart.setIsActive(false);
        shoppingCartRepository.save(cart);
    }

    @Override
    public ShoppingCartDto removeFromCart(RemoveFromCartRequest request) {
        Cart cart = shoppingCartRepository.findByUsernameAndIsActiveTrue(request.username())
                .orElseThrow(() -> noCartByUser(request.username()));

        List<UUID> notFound = request.productsIds().stream()
                .filter(id -> !cart.getProducts().containsKey(id))
                .toList();

        if (!notFound.isEmpty()) {
            throw new NoProductsInShoppingCartException("Cart '%s' has no such products: '%s'"
                    .formatted(cart.getShoppingCartId(), notFound));
        }

        request.productsIds().forEach(id -> cart.getProducts().remove(id));
        shoppingCartRepository.save(cart);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto changeQuantity(ChangeQuantityRequest request) {
        Cart cart = shoppingCartRepository.findByUsernameAndIsActiveTrue(request.username())
                .orElseThrow(() -> noCartByUser(request.username()));

        if (!cart.getProducts().containsKey(request.productId())) {
            throw new NoProductsInShoppingCartException("Cart '%s' has no product: '%s'"
                    .formatted(cart.getShoppingCartId(), request.productId()));
        }

        cart.getProducts().put(request.productId(), request.newQuantity());
        shoppingCartRepository.save(cart);
        return shoppingCartMapper.toDto(cart);
    }

    private CartNotFoundException noCartByUser(String username) {
        return new CartNotFoundException("Cart for user '%s' not found"
                .formatted(username));
    }
}
