package ru.practicum.interaction.api.api;

import org.springframework.data.domain.Pageable;
import ru.practicum.interaction.api.dto.request.CreateProductRequest;
import ru.practicum.interaction.api.dto.request.UpdateProductRequest;
import ru.practicum.interaction.api.dto.response.PageProductDto;
import ru.practicum.interaction.api.dto.response.ProductDto;
import ru.practicum.interaction.api.enums.ProductCategory;
import ru.practicum.interaction.api.enums.QuantityState;

import java.util.UUID;

public interface ShoppingStoreApi {

    ProductDto getProduct(UUID productId);

    PageProductDto<ProductDto> getProducts(ProductCategory category, Pageable pageable);

    ProductDto createProduct(CreateProductRequest request);

    ProductDto updateProduct(UpdateProductRequest request);

    Boolean removeProductFromStore(UUID productId);

    Boolean updateQuantityState(UUID productId, QuantityState quantityState);
}
