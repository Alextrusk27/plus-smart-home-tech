package ru.practicum.shopping.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.ProductCategory;
import ru.practicum.interaction.api.QuantityState;
import ru.practicum.interaction.api.dto.ProductDto;
import ru.practicum.shopping.store.dto.CreateProductRequest;
import ru.practicum.shopping.store.dto.PageProductDto;
import ru.practicum.shopping.store.dto.UpdateProductRequest;
import ru.practicum.shopping.store.service.ProductService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable UUID productId) {
        log.info("Fetching product: {}", productId);
        var productDto = productService.getProduct(productId);
        log.debug("Product fetched successfully: id={}, name={}",
                productDto.productId(), productDto.productName());
        return ResponseEntity.ok(productDto);
    }

    @GetMapping
    public ResponseEntity<PageProductDto<ProductDto>> getProducts(@RequestParam ProductCategory category,
                                                                  Pageable pageable) {
        log.info("Fetching all products in category: {}", category);
        var products = productService.getProducts(category, pageable);
        log.debug("Products fetched successfully");
        return ResponseEntity.ok(PageProductDto.from(products));
    }

    @PutMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid CreateProductRequest request) {
        log.info("Creating new product: {}", request.productName());
        var productDto = productService.createProduct(request);
        log.debug("Product created successfully: id={}, name={}",
                productDto.productId(), productDto.productName());
        URI location = URI.create("/api/v1/shopping-store/product/" + productDto.productId());
        return ResponseEntity.created(location).body(productDto);
    }

    @PostMapping
    public ResponseEntity<ProductDto> updateProduct(@RequestBody @Valid UpdateProductRequest request) {
        log.info("Updating product: {}", request.productName());
        var productDto = productService.updateProduct(request);
        log.debug("Product updated successfully: id={}, name={}",
                productDto.productId(), productDto.productName());
        return ResponseEntity.ok(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public ResponseEntity<Boolean> removeProductFromStore(@RequestBody UUID productId) {
        log.info("Deactivating product from store: {}", productId);
        Boolean isRemoved = productService.removeProductFromStore(productId);
        log.debug("Product id={} deactivated successfully", productId);
        return ResponseEntity.ok(isRemoved);
    }

    @PostMapping("/quantityState")
    public ResponseEntity<Boolean> updateQuantityState(@RequestParam UUID productId,
                                                       @RequestParam QuantityState quantityState) {
        log.info("Updating quantity state: {} in product id={}", quantityState, productId);
        Boolean isUpdated = productService.updateQuantityState(productId, quantityState);
        log.debug("Product id={} updated successfully", productId);
        return ResponseEntity.ok(isUpdated);
    }
}
