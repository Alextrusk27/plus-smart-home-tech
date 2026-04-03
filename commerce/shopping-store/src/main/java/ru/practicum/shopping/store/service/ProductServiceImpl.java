package ru.practicum.shopping.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.api.ProductCategory;
import ru.practicum.interaction.api.ProductState;
import ru.practicum.interaction.api.QuantityState;
import ru.practicum.interaction.api.dto.request.CreateProductRequest;
import ru.practicum.interaction.api.dto.request.UpdateProductRequest;
import ru.practicum.interaction.api.dto.response.ProductDto;
import ru.practicum.interaction.api.exception.ProductNotFoundException;
import ru.practicum.shopping.store.mapper.ProductMapper;
import ru.practicum.shopping.store.model.Product;
import ru.practicum.shopping.store.repository.ProductRepository;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        return productRepository.findById(productId)
                .map(productMapper::toDto)
                .orElseThrow(() -> notFound(productId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        return productRepository.findByProductCategory(category, pageable)
                .map(productMapper::toDto);
    }

    @Override
    public ProductDto createProduct(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    public ProductDto updateProduct(UpdateProductRequest request) {
        return productRepository.findById(request.productId())
                .map(p -> {
                    productMapper.updateEntity(p, request);
                    return p;
                })
                .map(productMapper::toDto)
                .orElseThrow(() -> notFound(request.productId()));
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> notFound(productId));
        product.setProductState(ProductState.DEACTIVATE);
        return true;
    }

    @Override
    public boolean updateQuantityState(UUID productId, QuantityState state) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> notFound(productId));
        product.setQuantityState(state);
        return true;
    }

    private ProductNotFoundException notFound(UUID productId) {
        return new ProductNotFoundException("Product with ID %s not found"
                .formatted(productId));
    }
}
