package ru.practicum.interaction.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.api.OrderApi;
import ru.practicum.interaction.api.dto.request.CreateNewOrderRequest;
import ru.practicum.interaction.api.dto.request.ProductReturnRequest;
import ru.practicum.interaction.api.dto.response.OrderDto;

import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient extends OrderApi {

    @Override
    @GetMapping
    Page<OrderDto> getOrders(@RequestParam String username, Pageable pageable);

    @Override
    @GetMapping("/by/payment")
    UUID getOrderIdByPaymentId(@RequestParam UUID paymentId);

    @Override
    @PutMapping
    OrderDto createOrder(@RequestParam String username, @RequestBody CreateNewOrderRequest request);

    @Override
    @PostMapping("/payment")
    OrderDto payment(@RequestBody UUID orderId);

    @Override
    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestBody UUID orderId);

    @Override
    @PostMapping("/assembly")
    OrderDto assembled(@RequestBody UUID orderId);

    @Override
    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@RequestBody UUID orderId);

    @Override
    @PostMapping("/delivery")
    OrderDto delivered(@RequestBody UUID orderId);

    @Override
    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestBody UUID orderId);

    @Override
    @PostMapping("/completed")
    OrderDto completed(@RequestBody UUID orderId);

    @Override
    @PostMapping("/canceled")
    OrderDto canceled(@RequestBody UUID orderId);

    @Override
    @PostMapping("/return")
    OrderDto returnProducts(@RequestBody ProductReturnRequest request);
}
