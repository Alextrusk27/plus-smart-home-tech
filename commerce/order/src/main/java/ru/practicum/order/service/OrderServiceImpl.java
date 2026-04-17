package ru.practicum.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.interaction.api.dto.request.CreateNewOrderRequest;
import ru.practicum.interaction.api.dto.request.ProductReturnRequest;
import ru.practicum.interaction.api.dto.response.OrderDto;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;
import ru.practicum.interaction.api.enums.OrderState;
import ru.practicum.interaction.api.exception.CartNotFoundException;
import ru.practicum.interaction.api.exception.NoOrderFoundException;
import ru.practicum.interaction.api.exception.NotAuthorizedUserException;
import ru.practicum.interaction.api.feign.PaymentClient;
import ru.practicum.interaction.api.feign.ShoppingCartClient;
import ru.practicum.interaction.api.feign.WarehouseClient;
import ru.practicum.order.model.Order;
import ru.practicum.order.model.mapper.OrderMapper;
import ru.practicum.order.repository.OrderRepository;

import java.math.BigDecimal;
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
    public OrderDto createOrder(String username, CreateNewOrderRequest request) {
        ShoppingCartDto shoppingCart = shoppingCartClient.getCart(username);

        log.warn("CART: {}", shoppingCart);

        if (!shoppingCart.equals(request.shoppingCart())) {
            throw new CartNotFoundException("Shopping cart not found");
        }

        Order order = orderMapper.toEntity(request);
        order.setUsername(username);

        // есть ли заказываемые товары на складе?

        order.setDeliveryWeight(BigDecimal.valueOf(5));
        order.setDeliveryVolume(BigDecimal.valueOf(5));
        order.setDeliveryPrice(BigDecimal.valueOf(5));
        order.setFragile(false);

        order.setProductPrice(paymentClient.productCost(orderMapper.toDto(order)));
        order.setTotalPrice(paymentClient.totalCost(orderMapper.toDto(order)));

        orderRepository.save(order);

        shoppingCartClient.deactivateCart(username);

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto initPayment(UUID orderId) {
        Order order = getOrderOrThrow(orderId);

        // запуск процесса оплаты в payment

        return updateOrderState(order, OrderState.ON_PAYMENT);
    }

    @Override
    public OrderDto paymentSuccess(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        return updateOrderState(order, OrderState.PAID);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        return updateOrderState(order, OrderState.PAYMENT_FAILED);
    }

    @Override
    public OrderDto initAssembly(UUID orderId) {
        Order order = getOrderOrThrow(orderId);

        // запуск процесса сборки в warehouse

        return updateOrderState(order, OrderState.ON_ASSEMBLY);
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
    public OrderDto initDelivery(UUID orderId) {
        Order order = getOrderOrThrow(orderId);

        // запуск процесса доставки в delivery

        return updateOrderState(order, OrderState.ON_DELIVERY);
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

        // изменение количества товаров в Warehouse

        return updateOrderState(order, OrderState.PRODUCT_RETURNED);
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        Order order = getOrderOrThrow(orderId);

        // посчитать
        return orderMapper.toDto(order);
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