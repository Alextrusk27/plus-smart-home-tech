package ru.practicum.shopping.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.api.ShoppingStoreApi;
import ru.practicum.interaction.api.dto.request.CreateProductRequest;
import ru.practicum.interaction.api.dto.request.UpdateProductRequest;
import ru.practicum.interaction.api.dto.response.PageProductDto;
import ru.practicum.interaction.api.dto.response.ProductDto;
import ru.practicum.interaction.api.enums.ProductCategory;
import ru.practicum.interaction.api.enums.QuantityState;
import ru.practicum.shopping.store.service.ShoppingStoreService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@Slf4j
public class ShoppingStoreController implements ShoppingStoreApi {
    private final ShoppingStoreService shoppingStoreService;

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId) {
        log.info("Fetching product: {}", productId);
        var productDto = shoppingStoreService.getProduct(productId);
        log.debug("Product fetched successfully: id={}, name={}",
                productDto.productId(), productDto.productName());
        return productDto;
    }

    @GetMapping
    public PageProductDto<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable) {
        log.info("Fetching all products in category: {}", category);
        var products = shoppingStoreService.getProducts(category, pageable);
        log.debug("Products fetched successfully");
        return PageProductDto.from(products);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@RequestBody @Valid CreateProductRequest request) {
        log.info("Creating new product: {}", request.productName());
        var productDto = shoppingStoreService.createProduct(request);
        log.debug("Product created successfully: id={}, name={}",
                productDto.productId(), productDto.productName());
        return productDto;
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid UpdateProductRequest request) {
        log.info("Updating product: {}", request.productName());
        var productDto = shoppingStoreService.updateProduct(request);
        log.debug("Product updated successfully: id={}, name={}",
                productDto.productId(), productDto.productName());
        return productDto;
    }

    @PostMapping("/removeProductFromStore")
    public Boolean removeProductFromStore(@RequestBody UUID productId) {
        log.info("Deactivating product from store: {}", productId);
        Boolean isRemoved = shoppingStoreService.removeProductFromStore(productId);
        log.debug("Product id={} deactivated successfully", productId);
        return isRemoved;
    }

    @PostMapping("/quantityState")
    public Boolean updateQuantityState(@RequestParam UUID productId,
                                       @RequestParam QuantityState quantityState) {
        log.info("Updating quantity state: {} in product id={}", quantityState, productId);
        Boolean isUpdated = shoppingStoreService.updateQuantityState(productId, quantityState);
        log.debug("Product id={} updated successfully", productId);
        return isUpdated;
    }
}
