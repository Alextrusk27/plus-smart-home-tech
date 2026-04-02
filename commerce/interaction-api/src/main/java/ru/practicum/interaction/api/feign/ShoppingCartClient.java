package ru.practicum.interaction.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.dto.request.ChangeQuantity;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;

import java.util.List;
import java.util.Map;

@FeignClient(name = "shopping-cart")
@RequestMapping("/api/v1/shopping-cart")
public interface ShoppingCartClient {

    @GetMapping
    ShoppingCartDto getCart(@RequestParam String username);

    @PutMapping
    ShoppingCartDto addToCart(@RequestParam String username, @RequestBody Map<String, Integer> products);

    @DeleteMapping
    void removeCart(@RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto removeFromCart(@RequestParam String username, @RequestBody List<String> productsIds);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantity(@RequestParam String username, @RequestParam ChangeQuantity changeQuantity);
}
