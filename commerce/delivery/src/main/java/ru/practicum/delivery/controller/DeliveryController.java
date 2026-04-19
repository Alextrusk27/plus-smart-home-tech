package ru.practicum.delivery.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.delivery.service.DeliveryService;
import ru.practicum.interaction.api.api.DeliveryApi;
import ru.practicum.interaction.api.dto.request.DeliveryRequest;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController implements DeliveryApi {
    private final DeliveryService deliveryService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID planDelivery(@RequestBody DeliveryRequest request) {
        log.info("Request to plan delivery for order: {}", request.orderId());
        var result = deliveryService.planDelivery(request);
        log.debug("Delivery planned with id: {}. Order: {}", result, request.orderId());
        return result;
    }

    @Override
    @PostMapping("/cost")
    public BigDecimal deliveryCost(@RequestBody UUID deliveryId) {
        log.info("Request to calculate delivery cost for order: {}", deliveryId);
        var result = deliveryService.deliveryCost(deliveryId);
        log.debug("Delivery cost calculated: {}. Delivery: {}", result, deliveryId);
        return result;
    }

    @Override
    @PostMapping("/picked")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deliveryPicked(@RequestBody UUID orderId) {
        log.info("Request to mark delivery as picked for order: {}", orderId);
        deliveryService.deliveryPicked(orderId);
        log.debug("Delivery marked as picked. Order: {}", orderId);
    }

    @Override
    @PostMapping("/successful")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deliverySuccessful(@RequestBody UUID orderId) {
        log.info("Request to mark delivery as successful for order: {}", orderId);
        deliveryService.deliverySuccessful(orderId);
        log.debug("Delivery marked as successful. Order: {}", orderId);
    }

    @Override
    @PostMapping("/failed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deliveryFailed(@RequestBody UUID orderId) {
        log.info("Request to mark delivery as failed for order: {}", orderId);
        deliveryService.deliveryFailed(orderId);
        log.debug("Delivery marked as failed. Order: {}", orderId);
    }

    @Override
    @PostMapping("/cancelled")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deliveryCancelled(@RequestBody UUID orderId) {
        log.info("Request to mark delivery as cancelled for order: {}", orderId);
        deliveryService.deliveryCancelled(orderId);
        log.debug("Delivery marked as cancelled. Order: {}", orderId);
    }
}