package ru.practicum.shopping.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.interaction.api.ProductCategory;
import ru.practicum.interaction.api.QuantityState;
import ru.practicum.interaction.api.dto.response.ProductDto;
import ru.practicum.shopping.store.dto.CreateProductRequest;
import ru.practicum.shopping.store.dto.UpdateProductRequest;

import java.util.UUID;

public interface ProductService {

    ProductDto getProduct(UUID productId);

    Page<ProductDto> getProducts(ProductCategory category, Pageable pageable);

    ProductDto createProduct(CreateProductRequest request);

    ProductDto updateProduct(UpdateProductRequest request);

    boolean removeProductFromStore(UUID productId);

    boolean updateQuantityState(UUID productId, QuantityState state);
}
