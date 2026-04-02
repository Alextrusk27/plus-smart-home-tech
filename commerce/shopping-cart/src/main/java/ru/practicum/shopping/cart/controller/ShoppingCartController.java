package ru.practicum.shopping.cart.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.dto.request.ChangeQuantity;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;
import ru.practicum.interaction.api.dto.request.AddToCartRequest;
import ru.practicum.interaction.api.dto.request.ChangeQuantityRequest;
import ru.practicum.interaction.api.dto.request.RemoveFromCartRequest;
import ru.practicum.shopping.cart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ResponseEntity<ShoppingCartDto> getCart(@RequestParam @NotBlank String username) {

        log.info("Fetching cart for user {}", username);
        var response = shoppingCartService.getCart(username);
        log.debug("Cart for user {} fetched successfully with id '{}'", username, response.shoppingCartId());
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ShoppingCartDto> addToCart(
            @RequestParam @NotBlank String username,
            @RequestBody @NotNull @NotEmpty Map<String, Integer> products) {

        log.info("User '{}' adding '{}' products to cart", username, products.size());
        AddToCartRequest request = AddToCartRequest.of(username, products);
        var response = shoppingCartService.addToCart(request);
        log.debug("User '{}' successfully added products to cart", username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeCart(@RequestParam @NotBlank String username) {

        log.info("User '{}' removing cart", username);
        shoppingCartService.removeCart(username);
        log.debug("User '{}' successfully removed cart", username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/remove")
    public ResponseEntity<ShoppingCartDto> removeFromCart(@RequestParam @NotBlank String username,
                                                          @RequestBody @NotNull @NotEmpty List<String> productsIds) {

        log.info("User '{}' removing '{}' products from cart", username, productsIds.size());
        RemoveFromCartRequest request = RemoveFromCartRequest.of(username, productsIds);
        var response = shoppingCartService.removeFromCart(request);
        log.debug("User '{}' successfully removed products from cart", username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-quantity")
    public ResponseEntity<ShoppingCartDto> changeQuantity(@RequestParam @NotBlank String username,
                                                          @RequestBody @Valid ChangeQuantity changeQuantity) {

        log.info("User '{}' changing product ID '{}'  quantity to '{}'", username, changeQuantity.productId(),
                changeQuantity.newQuantity());
        ChangeQuantityRequest request = ChangeQuantityRequest.of(username, changeQuantity);
        var response = shoppingCartService.changeQuantity(request);
        log.debug("User '{}' successfully changed quantity product ID '{}'", username, changeQuantity.productId());
        return ResponseEntity.ok(response);
    }
}
