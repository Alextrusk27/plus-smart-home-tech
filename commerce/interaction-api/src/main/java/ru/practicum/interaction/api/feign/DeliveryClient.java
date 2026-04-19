package ru.practicum.interaction.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.interaction.api.api.DeliveryApi;
import ru.practicum.interaction.api.dto.request.DeliveryRequest;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient extends DeliveryApi {

    @Override
    @PostMapping
    UUID planDelivery(@RequestBody DeliveryRequest request);

    @Override
    @PostMapping("/cost")
    BigDecimal deliveryCost(@RequestBody UUID deliveryId);

    @Override
    @PostMapping("/picked")
    void deliveryPicked(@RequestBody UUID orderId);

    @Override
    @PostMapping("/successful")
    void deliverySuccessful(@RequestBody UUID orderId);

    @Override
    @PostMapping("/failed")
    void deliveryFailed(@RequestBody UUID orderId);
}