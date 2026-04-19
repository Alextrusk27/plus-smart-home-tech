package ru.practicum.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.api.dto.request.*;
import ru.practicum.interaction.api.dto.response.BookedProductsDto;
import ru.practicum.interaction.api.dto.response.OrderDto;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;
import ru.practicum.interaction.api.enums.OrderState;
import ru.practicum.interaction.api.exception.CartNotFoundException;
import ru.practicum.interaction.api.exception.NoOrderFoundException;
import ru.practicum.interaction.api.exception.NotAuthorizedUserException;
import ru.practicum.interaction.api.exception.OrderCreationFailedException;
import ru.practicum.interaction.api.feign.DeliveryClient;
import ru.practicum.interaction.api.feign.PaymentClient;
import ru.practicum.interaction.api.feign.ShoppingCartClient;
import ru.practicum.interaction.api.feign.WarehouseClient;
import ru.practicum.order.model.Order;
import ru.practicum.order.model.mapper.OrderMapper;
import ru.practicum.order.repository.OrderRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private final ShoppingCartClient shoppingCartClient;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @Override
    public Page<OrderDto> getOrders(String username, Pageable pageable) {
        checkUsername(username);
        return orderRepository.findAllByUsername(username, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public UUID getOrderIdByPayment(UUID paymentId) {
        if (paymentId == null) {
            throw new NoOrderFoundException("Payment id is null");
        }

        return orderRepository.findOrderIdByPaymentId(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Order with payment id '%s' not found"
                        .formatted(paymentId)));
    }

    @Override
    @Transactional
    public OrderDto createOrder(String username, CreateNewOrderRequest request) {
        checkShoppingCart(username, request);
        BookedProductsDto bookedProducts = reserveProductsInWarehouse(request);
        Order order = saveBaseOrder(request, username, bookedProducts);

        try {
            planDelivery(request, order);
            planPayment(order);
            shoppingCartClient.deactivateCart(username);
            return orderMapper.toDto(order);
        } catch (Exception e) {
            String failedStage = order.getPaymentId() != null ? "payment"
                    : order.getDeliveryId() != null ? "delivery"
                    : "warehouse";
            log.error("Order creation failed at stage: {} for user {}", failedStage, username, e);

            if (order.getPaymentId() != null) {
                try {
                    paymentClient.paymentCancelled(order.getPaymentId());
                    log.warn("Payment {} marked as CANCELLED", order.getPaymentId());
                } catch (Exception paymentException) {
                    log.error("CRITICAL: Failed to refund payment {}",
                            order.getPaymentId(), paymentException);
                }
            }

            if (order.getDeliveryId() != null) {
                try {
                    deliveryClient.deliveryCancelled(order.getDeliveryId());
                    log.warn("Delivery {} marked as CANCELLED", order.getDeliveryId());
                } catch (Exception deliverylException) {
                    log.error("CRITICAL: Failed to cancel delivery {}",
                            order.getDeliveryId(), deliverylException);
                }
            }

            try {
                warehouseClient.acceptReturn(request.shoppingCart().products());
                log.warn("Products returned to warehouse");
            } catch (Exception returnException) {
                log.error("CRITICAL: Failed to return products", returnException);
            }
            throw new OrderCreationFailedException("Order creation failed: " + e.getMessage());
        }
    }

    @Override
    public OrderDto payment(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        return updateOrderState(order, OrderState.PAID);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        return updateOrderState(order, OrderState.PAYMENT_FAILED);
    }

    @Override
    public OrderDto orderAssembled(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        return updateOrderState(order, OrderState.ASSEMBLED);
    }

    @Override
    public OrderDto orderAssemblyFailed(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        return updateOrderState(order, OrderState.ASSEMBLY_FAILED);
    }

    @Override
    public OrderDto orderDelivered(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        return updateOrderState(order, OrderState.DELIVERED);
    }

    @Override
    public OrderDto orderDeliveryFailed(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        return updateOrderState(order, OrderState.DELIVERY_FAILED);
    }

    @Override
    public OrderDto completed(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        return updateOrderState(order, OrderState.COMPLETED);
    }

    @Override
    public OrderDto canceled(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        return updateOrderState(order, OrderState.CANCELED);
    }

    @Override
    public OrderDto returnProducts(ProductReturnRequest request) {
        Order order = getOrderOrThrow(request.orderId());
        warehouseClient.acceptReturn(request.products());
        return updateOrderState(order, OrderState.PRODUCT_RETURNED);
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        Order order = getOrderOrThrow(orderId);

        // посчитать
        return orderMapper.toDto(order);
    }

    private void checkShoppingCart(String username, CreateNewOrderRequest request) {
        ShoppingCartDto cart = shoppingCartClient.getCart(username);
        if (!cart.products().equals(request.shoppingCart().products())) {
            throw new CartNotFoundException("Shopping cart does not match");
        }
    }

    private BookedProductsDto reserveProductsInWarehouse(CreateNewOrderRequest request) {
        return warehouseClient.assemblyForOrder(
                new AssemblyProductsForOrderRequest(request.shoppingCart().products(), null));
    }

    private Order saveBaseOrder(CreateNewOrderRequest request, String username, BookedProductsDto bookedProducts) {
        Order order = orderMapper.toEntity(request);
        order.setUsername(username);
        order.setDeliveryWeight(bookedProducts.deliveryWeight());
        order.setDeliveryVolume(bookedProducts.deliveryVolume());
        order.setFragile(bookedProducts.fragile());
        return orderRepository.save(order);
    }

    private void planDelivery(CreateNewOrderRequest request, Order order) {
        UUID deliveryId = deliveryClient.planDelivery(DeliveryRequest.of(
                AddressRequest.of(request.deliveryAddress()),
                AddressRequest.of(warehouseClient.getAddress()),
                orderMapper.toDto(order)
        ));

        order.setDeliveryId(deliveryId);
        order.setDeliveryPrice(deliveryClient.deliveryCost(deliveryId));
    }

    private void planPayment(Order order) {
        order.setProductPrice(paymentClient.productCost(orderMapper.toDto(order)));
        order.setTotalPrice(order.getProductPrice().add(order.getDeliveryPrice()));
        order.setPaymentId(paymentClient.payment(orderMapper.toDto(order)).paymentId());
    }

    private OrderDto updateOrderState(Order order, OrderState state) {
        order.setState(state);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    private Order getOrderOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order %s not found"
                        .formatted(orderId)));
    }

    private void checkUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Username required");
        }
    }
}