package ru.practicum.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.api.PaymentApi;
import ru.practicum.interaction.api.dto.response.OrderDto;
import ru.practicum.interaction.api.dto.response.PaymentDto;
import ru.practicum.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
@Slf4j
public class PaymentController implements PaymentApi {
    private final PaymentService paymentService;

    @Override
    @PostMapping
    public PaymentDto payment(@RequestBody OrderDto order) {
        log.info("Request to create payment from order: {}", order.orderId());
        var result = paymentService.payment(order);
        log.debug("Payment created: {}", result);
        return result;
    }

    @Override
    @PostMapping("/productCost")
    public BigDecimal productCost(@RequestBody OrderDto order) {
        log.info("Request to calculate product cost from order: {}", order.orderId());
        var result = paymentService.calculateProductCost(order);
        log.debug("Product cost calculated: {}. Order: {}", result, order.orderId());
        return result;
    }

    @Override
    @PostMapping("/totalCost")
    public BigDecimal totalCost(@RequestBody OrderDto order) {
        log.info("Request to calculate total cost from order: {}", order.orderId());
        var result = paymentService.calculateTotalCost(order);
        log.debug("Total cost calculated: {}. Order: {}", result, order.orderId());
        return result;
    }

    @Override
    @PostMapping("/refund")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void paymentSuccess(@RequestBody UUID orderId) {
        log.info("Request to payment success from order: {}", orderId);
        paymentService.paymentSuccess(orderId);
        log.debug("Payment successful. Order: {}", orderId);
    }

    @Override
    @PostMapping("/failed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void paymentFailed(@RequestBody UUID orderId) {
        log.info("Request to payment failed from order: {}", orderId);
        paymentService.paymentFailed(orderId);
        log.debug("Payment failed. Order: {}", orderId);
    }

    @Override
    @PostMapping("/cancelled")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void paymentCancelled(@RequestBody UUID orderId) {
        log.info("Request to payment cancelled from order: {}", orderId);
        paymentService.paymentFailed(orderId);
        log.debug("Payment cancelled. Order: {}", orderId);
    }
}
