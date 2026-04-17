package ru.practicum.interaction.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.interaction.api.api.PaymentApi;
import ru.practicum.interaction.api.dto.response.OrderDto;
import ru.practicum.interaction.api.dto.response.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient extends PaymentApi {

    @Override
    @PostMapping
    PaymentDto payment(@RequestBody OrderDto order);

    @Override
    @PostMapping("/productCost")
    BigDecimal productCost(@RequestBody OrderDto order);

    @Override
    @PostMapping("/totalCost")
    BigDecimal totalCost(@RequestBody OrderDto order);

    @Override
    @PostMapping("/refund")
    void paymentSuccess(@RequestBody UUID orderId);

    @Override
    @PostMapping("/failed")
    void paymentFailed(@RequestBody UUID orderId);
}
