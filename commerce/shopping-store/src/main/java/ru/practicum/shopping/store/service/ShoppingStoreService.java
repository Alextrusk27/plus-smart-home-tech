package ru.practicum.shopping.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.interaction.api.enums.ProductCategory;
import ru.practicum.interaction.api.enums.QuantityState;
import ru.practicum.interaction.api.dto.request.CreateProductRequest;
import ru.practicum.interaction.api.dto.request.UpdateProductRequest;
import ru.practicum.interaction.api.dto.response.ProductDto;

import java.util.UUID;

public interface ShoppingStoreService {

    ProductDto getProduct(UUID productId);

    Page<ProductDto> getProducts(ProductCategory category, Pageable pageable);

    ProductDto createProduct(CreateProductRequest request);

    ProductDto updateProduct(UpdateProductRequest request);

    boolean removeProductFromStore(UUID productId);

    boolean updateQuantityState(UUID productId, QuantityState state);
}
