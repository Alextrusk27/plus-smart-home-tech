package ru.practicum.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.api.OrderApi;
import ru.practicum.interaction.api.dto.request.CreateNewOrderRequest;
import ru.practicum.interaction.api.dto.request.ProductReturnRequest;
import ru.practicum.interaction.api.dto.response.OrderDto;
import ru.practicum.interaction.api.enums.OrderState;
import ru.practicum.order.service.OrderService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController implements OrderApi {
    private final OrderService orderService;

    @Override
    @GetMapping
    public Page<OrderDto> getOrders(@RequestParam String username, Pageable pageable) {
        log.info("Fetching orders for user {}", username);
        var result = orderService.getOrders(username, pageable);
        log.debug("Orders for user {} fetched successfully", username);
        return result;
    }

    @Override
    @PutMapping
    public OrderDto createOrder(@RequestParam String username, @RequestBody @Valid CreateNewOrderRequest request) {
        log.info("Request from user {} to create order: {}", username, request);
        var result = orderService.createOrder(username, request);
        log.debug("Created order: {}", result);
        return result;
    }

    @Override
    @PostMapping("/payment")
    public OrderDto initPayment(@RequestBody UUID orderId) {
        log.info("Request to init payment for order: {}", orderId);
        var result = orderService.initPayment(orderId);
        log.debug("Payment for order {} initialized", orderId);
        return result;
    }

    @Override
    @PostMapping("/payment/success")
    public OrderDto paymentSuccess(@RequestBody UUID orderId) {
        log.info("Processing payment success for order: {}", orderId);
        var result = orderService.paymentSuccess(orderId);
        writeStatusLog(orderId, result.state());
        return result;
    }

    @Override
    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(@RequestBody UUID orderId) {
        log.info("Processing payment fail for order: {}", orderId);
        var result = orderService.paymentFailed(orderId);
        writeStatusLog(orderId, result.state());
        return result;
    }

    @Override
    @PostMapping("/assembly")
    public OrderDto initAssembly(@RequestBody UUID orderId) {
        log.info("Request to init assembly for order: {}", orderId);
        var result = orderService.initAssembly(orderId);
        writeStatusLog(orderId, result.state());
        return result;
    }

    @Override
    @PostMapping("/assembly/success")
    public OrderDto assembled(@RequestBody UUID orderId) {
        log.info("Processing order assembled for order: {}", orderId);
        var result = orderService.orderAssembled(orderId);
        writeStatusLog(orderId, result.state());
        return result;
    }

    @Override
    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(@RequestBody UUID orderId) {
        log.info("Processing order assembly failed for order: {}", orderId);
        var result = orderService.orderAssemblyFailed(orderId);
        writeStatusLog(orderId, result.state());
        return result;
    }

    @Override
    @PostMapping("/delivery")
    public OrderDto initDelivery(@RequestBody UUID orderId) {
        log.info("Request to init delivery for order: {}", orderId);
        var result = orderService.initDelivery(orderId);
        writeStatusLog(orderId, result.state());
        return result;
    }

    @Override
    @PostMapping("/delivery/success")
    public OrderDto delivered(@RequestBody UUID orderId) {
        log.info("Processing order delivered for order: {}", orderId);
        var result = orderService.orderDelivered(orderId);
        writeStatusLog(orderId, result.state());
        return result;
    }

    @Override
    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailed(@RequestBody UUID orderId) {
        log.info("Processing order delivery failed for order: {}", orderId);
        var result = orderService.orderDeliveryFailed(orderId);
        writeStatusLog(orderId, result.state());
        return result;
    }

    @Override
    @PostMapping("/completed")
    public OrderDto completed(@RequestBody UUID orderId) {
        log.info("Processing order: {} completed", orderId);
        var result = orderService.completed(orderId);
        writeStatusLog(orderId, result.state());
        return result;
    }

    @Override
    @PostMapping("/canceled")
    public OrderDto canceled(@RequestBody UUID orderId) {
        log.info("Processing order: {} canceled", orderId);
        var result = orderService.canceled(orderId);
        writeStatusLog(orderId, result.state());
        return null;
    }

    @Override
    @PostMapping("/return")
    public OrderDto returnProducts(@RequestBody ProductReturnRequest request) {
        log.info("Request to returning order: {}", request.orderId());
        var result = orderService.returnProducts(request);
        log.debug("Order {} successfully returned", request.orderId());
        return result;
    }

    @Override
    @PostMapping("/calculate/total")
    public OrderDto calculateTotal(@RequestBody UUID orderId) {
        log.info("Calculating total price for order: {}", orderId);
        var result = orderService.calculateTotal(orderId);
        log.debug("Total price for order {} is {}", orderId, result.totalPrice());
        return null;
    }

    private void writeStatusLog(UUID orderId, OrderState state) {
        log.debug("Order {} status updated to: {}", orderId, state);
    }
}