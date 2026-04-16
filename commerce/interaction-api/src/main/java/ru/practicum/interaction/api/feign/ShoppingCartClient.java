package ru.practicum.interaction.api.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.api.ShoppingCartApi;
import ru.practicum.interaction.api.dto.request.ChangeQuantity;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;

import java.util.List;
import java.util.Map;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient extends ShoppingCartApi {

    @Override
    @GetMapping
    ShoppingCartDto getCart(@RequestParam String username);

    @Override
    @PutMapping
    ShoppingCartDto addToCart(@RequestParam String username,
                              @RequestBody Map<String, Integer> products);

    @Override
    @DeleteMapping
    void deactivateCart(@RequestParam String username);

    @Override
    @PostMapping("/remove")
    ShoppingCartDto removeFromCart(@RequestParam String username,
                                   @RequestBody List<String> productsIds);

    @Override
    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantity(@RequestParam String username,
                                   @RequestBody @Valid ChangeQuantity changeQuantity);
}
