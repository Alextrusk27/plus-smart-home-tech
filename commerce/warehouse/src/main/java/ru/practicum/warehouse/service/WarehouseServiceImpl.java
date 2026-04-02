package ru.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.api.dto.request.ChangeQuantity;
import ru.practicum.interaction.api.dto.request.NewProductInWarehouseRequest;
import ru.practicum.interaction.api.dto.response.AddressDto;
import ru.practicum.interaction.api.dto.response.BookedProductsDto;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;
import ru.practicum.interaction.api.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.practicum.interaction.api.exception.ProductNotFoundException;
import ru.practicum.interaction.api.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.practicum.warehouse.mapper.ProductMapper;
import ru.practicum.warehouse.model.Product;
import ru.practicum.warehouse.repository.ProductRepository;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private static final String[] ADDRESSES =
            new String[] {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    @Override
    public void createProduct(NewProductInWarehouseRequest request) {
        if (productRepository.existsById(request.productId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Product '%s' already exists in warehouse"
                    .formatted(request.productId()));
        }
        productRepository.save(productMapper.toEntity(request));
    }

    @Override
    @Transactional(readOnly = true)
    public BookedProductsDto checkProduct(ShoppingCartDto shoppingCart) {
        Map<UUID, Integer> requested = shoppingCart.products();
        List<Product> products = productRepository.findAllById(requested.keySet());

        checkMissingProducts(requested, products);

        List<Product> notEnough = products.stream()
                .filter(product -> product.getQuantity() < requested.get(product.getProductId()))
                .toList();

        if (!notEnough.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("Not enough products in warehouse: [%s]"
                    .formatted(notEnoughProductsMessage(notEnough, requested)));
        }

        return new BookedProductsDto(
                products.stream().map(p -> p.getWeight()
                                .multiply(BigDecimal.valueOf(requested.get(p.getProductId()))))
                        .reduce(BigDecimal.ZERO, BigDecimal::add),

                products.stream().map(p -> p.getDimension().getWidth()
                                .multiply(p.getDimension().getHeight())
                                .multiply(p.getDimension().getDepth())
                                .multiply(BigDecimal.valueOf(requested.get(p.getProductId()))))
                        .reduce(BigDecimal.ZERO, BigDecimal::add),

                products.stream().anyMatch(Product::getFragile)
        );
    }

    @Override
    public void addProduct(ChangeQuantity changeQuantity) {
        Product product = productRepository.findById(changeQuantity.productId())
                .orElseThrow(() -> new ProductNotFoundException("Product %s not found"
                        .formatted(changeQuantity.productId())));
        product.setQuantity(changeQuantity.newQuantity());
    }

    @Override
    public AddressDto getAddress() {
        return new AddressDto(
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS
        );
    }

    private void checkMissingProducts(Map<UUID, Integer> requested, List<Product> products) {
        if (products.size() != requested.size()) {
            Set<UUID> foundIds = products.stream()
                    .map(Product::getProductId)
                    .collect(Collectors.toSet());

            List<UUID> missing = requested.keySet().stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new ProductNotFoundException("Products not found: " + missing);
        }
    }

    private String notEnoughProductsMessage(List<Product> notEnough, Map<UUID, Integer> requested) {
        return notEnough.stream()
                .map(product -> product.getProductId() +
                        " (available: " + product.getQuantity() +
                        ", requested: " + requested.get(product.getProductId()) + ")")
                .collect(Collectors.joining(", "));
    }
}
