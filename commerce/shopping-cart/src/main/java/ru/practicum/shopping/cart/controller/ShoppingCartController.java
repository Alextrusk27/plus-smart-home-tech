package ru.practicum.shopping.cart.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.api.ShoppingCartApi;
import ru.practicum.interaction.api.dto.request.AddToCartRequest;
import ru.practicum.interaction.api.dto.request.ChangeQuantity;
import ru.practicum.interaction.api.dto.request.ChangeQuantityRequest;
import ru.practicum.interaction.api.dto.request.RemoveFromCartRequest;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;
import ru.practicum.shopping.cart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ShoppingCartController implements ShoppingCartApi {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ShoppingCartDto getCart(@RequestParam @NotBlank String username) {

        log.info("Fetching cart for user {}", username);
        var result = shoppingCartService.getCart(username);
        log.debug("Cart for user {} fetched successfully with id '{}'", username, result.shoppingCartId());
        return result;
    }

    @PutMapping
    public ShoppingCartDto addToCart(
            @RequestParam @NotBlank String username,
            @RequestBody @NotNull @NotEmpty Map<String, Integer> products) {

        log.info("User '{}' adding '{}' products to cart", username, products);
        var result = shoppingCartService.addToCart(AddToCartRequest.of(username, products));
        log.debug("User '{}' successfully added products to cart", username);
        return result;
    }

    @DeleteMapping
    public void removeCart(@RequestParam @NotBlank String username) {

        log.info("User '{}' removing cart", username);
        shoppingCartService.removeCart(username);
        log.debug("User '{}' successfully removed cart", username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeFromCart(@RequestParam @NotBlank String username,
                                          @RequestBody @NotNull @NotEmpty List<String> productsIds) {

        log.info("User '{}' removing '{}' products from cart", username, productsIds.size());
        RemoveFromCartRequest request = RemoveFromCartRequest.of(username, productsIds);
        var result = shoppingCartService.removeFromCart(request);
        log.debug("User '{}' successfully removed products from cart", username);
        return result;
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantity(@RequestParam @NotBlank String username,
                                          @RequestBody @Valid ChangeQuantity changeQuantity) {

        log.info("User '{}' changing product ID '{}'  quantity to '{}'", username, changeQuantity.productId(),
                changeQuantity.newQuantity());
        var result = shoppingCartService.changeQuantity(ChangeQuantityRequest.of(username, changeQuantity));
        log.debug("User '{}' successfully changed quantity product ID '{}'", username, changeQuantity.productId());
        return result;
    }
}
