package ru.practicum.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.interaction.api.dto.request.CreateNewOrderRequest;
import ru.practicum.interaction.api.dto.request.ProductReturnRequest;
import ru.practicum.interaction.api.dto.response.OrderDto;

import java.util.UUID;

public interface OrderService {

    Page<OrderDto> getOrders(String username, Pageable pageable);

    OrderDto createOrder(String username, CreateNewOrderRequest request);

    OrderDto returnProducts(ProductReturnRequest request);

    OrderDto initPayment(UUID orderId);

    OrderDto paymentSuccess(UUID orderId);

    OrderDto paymentFailed(UUID orderId);

    OrderDto initAssembly(UUID orderId);

    OrderDto orderAssembled(UUID orderId);

    OrderDto orderAssemblyFailed(UUID orderId);

    OrderDto initDelivery(UUID orderId);

    OrderDto orderDelivered(UUID orderId);

    OrderDto orderDeliveryFailed(UUID orderId);

    OrderDto completed(UUID orderId);

    OrderDto canceled(UUID orderId);

    OrderDto calculateTotal(UUID orderId);
}
