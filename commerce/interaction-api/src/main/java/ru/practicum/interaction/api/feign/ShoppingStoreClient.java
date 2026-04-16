package ru.practicum.interaction.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.api.ShoppingStoreApi;
import ru.practicum.interaction.api.dto.request.CreateProductRequest;
import ru.practicum.interaction.api.dto.request.UpdateProductRequest;
import ru.practicum.interaction.api.dto.response.PageProductDto;
import ru.practicum.interaction.api.dto.response.ProductDto;
import ru.practicum.interaction.api.enums.ProductCategory;
import ru.practicum.interaction.api.enums.QuantityState;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient extends ShoppingStoreApi {

    @Override
    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);

    @Override
    @GetMapping("/batch")
    List<ProductDto> getProductsByIds(@RequestParam List<UUID> productIds);

    @Override
    @GetMapping
    PageProductDto<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable);

    @Override
    @PutMapping
    ProductDto createProduct(@RequestBody CreateProductRequest request);

    @Override
    @PostMapping
    ProductDto updateProduct(@RequestBody UpdateProductRequest request);

    @Override
    @PostMapping("/removeProductFromStore")
    Boolean removeProductFromStore(@RequestBody UUID productId);

    @Override
    @PostMapping("/quantityState")
    Boolean updateQuantityState(@RequestParam UUID productId, @RequestParam QuantityState quantityState);
}
