package ru.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.api.dto.request.AddProductToWarehouseRequest;
import ru.practicum.interaction.api.dto.request.AssemblyProductsForOrderRequest;
import ru.practicum.interaction.api.dto.request.NewProductInWarehouseRequest;
import ru.practicum.interaction.api.dto.request.ShippedToDeliveryRequest;
import ru.practicum.interaction.api.dto.response.AddressDto;
import ru.practicum.interaction.api.dto.response.BookedProductsDto;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;
import ru.practicum.interaction.api.exception.NoOrderBookingFoundException;
import ru.practicum.interaction.api.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.practicum.interaction.api.exception.ProductNotFoundException;
import ru.practicum.interaction.api.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.practicum.warehouse.mapper.OrderBookingMapper;
import ru.practicum.warehouse.mapper.ProductMapper;
import ru.practicum.warehouse.model.OrderBooking;
import ru.practicum.warehouse.model.Product;
import ru.practicum.warehouse.repository.OrderBookingRepository;
import ru.practicum.warehouse.repository.WarehouseRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.practicum.interaction.api.constants.WarehouseConstants.CURRENT_ADDRESS;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final OrderBookingRepository orderBookingRepository;
    private final ProductMapper productMapper;
    private final OrderBookingMapper orderBookingMapper;

    @Override
    public void createProduct(NewProductInWarehouseRequest request) {
        if (warehouseRepository.existsById(request.productId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Product '%s' already exists in warehouse"
                    .formatted(request.productId()));
        }
        warehouseRepository.save(productMapper.toEntity(request));
    }

    @Override
    public BookedProductsDto checkProduct(ShoppingCartDto shoppingCart) {
        return checkProduct(shoppingCart, false);
    }

    @Override
    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        OrderBooking booking = orderBookingRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new NoOrderBookingFoundException("No bookings found for order %s"
                        .formatted(request.orderId())));

        booking.setDeliveryId(request.deliveryId());
    }

    @Override
    @Transactional
    public void addProduct(AddProductToWarehouseRequest request) {
        Product product = findProductOrThrow(request.productId());
        product.setQuantity(product.getQuantity() + request.quantity());
    }

    @Override
    @Transactional
    public void returnProducts(Map<UUID, Integer> products) {
        products.forEach(warehouseRepository::increaseQuantity);
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyForOrder(AssemblyProductsForOrderRequest request) {
        BookedProductsDto bookedProducts = checkProduct(new ShoppingCartDto(null, request.products()), true);
        OrderBooking orderBooking = orderBookingMapper.toEntity(request);
        orderBookingRepository.save(orderBooking);
        request.products().forEach(warehouseRepository::decreaseQuantity);
        return bookedProducts;
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

    private Product findProductOrThrow(UUID productId) {
        return warehouseRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product %s not found"
                        .formatted(productId)));
    }

    private BookedProductsDto checkProduct(ShoppingCartDto shoppingCart, boolean locked) {
        Map<UUID, Integer> requested = shoppingCart.products();
        List<Product> products = findAllWithLock(requested.keySet(), locked);

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

    private List<Product> findAllWithLock(Set<UUID> productIds, boolean locked) {
        return locked ? warehouseRepository.findAllByIdWithLock(productIds)
                : warehouseRepository.findAllById(productIds);
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
