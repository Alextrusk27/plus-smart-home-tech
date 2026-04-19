package ru.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.interaction.api.dto.response.OrderDto;
import ru.practicum.interaction.api.dto.response.PaymentDto;
import ru.practicum.interaction.api.dto.response.ProductDto;
import ru.practicum.interaction.api.enums.PaymentState;
import ru.practicum.interaction.api.exception.NotEnoughInfoInOrderToCalculateException;
import ru.practicum.interaction.api.exception.ProductNotFoundException;
import ru.practicum.interaction.api.feign.OrderClient;
import ru.practicum.interaction.api.feign.ShoppingStoreClient;
import ru.practicum.payment.model.Payment;
import ru.practicum.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.interaction.api.constants.BaseRateConstants.VAT_MULTIPLIER;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Override
    public PaymentDto payment(OrderDto order) {
        if (order.productPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException(("Payment calculation error: " +
                    "Order '%s': product price is null").formatted(order.orderId()));
        }

        if (order.deliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException(("Payment calculation error: " +
                    "Order '%s': delivery price is null").formatted(order.orderId()));
        }

        if (order.totalPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException(("Payment calculation error: " +
                    "Order '%s': total price is null").formatted(order.orderId()));
        }

        Payment payment = Payment.builder()
                .totalPayment(order.totalPrice())
                .productsTotal(order.productPrice())
                .deliveryTotal(order.deliveryPrice())
                .state(PaymentState.PENDING)
                .build();

        paymentRepository.save(payment);

        return PaymentDto.of(
                payment.getPaymentId(),
                payment.getTotalPayment(),
                payment.getProductsTotal(),
                payment.getDeliveryTotal()
        );
    }

    @Override
    public BigDecimal calculateProductCost(OrderDto order) {
        Map<UUID, ProductDto> products = shoppingStoreClient.getProductsByIds(
                        new ArrayList<>(order.products().keySet()))
                .stream()
                .collect(Collectors.toMap(ProductDto::productId, Function.identity()));

        return order.products().entrySet().stream()
                .map(entry -> {
                    ProductDto product = products.get(entry.getKey());
                    if (product == null) {
                        throw new ProductNotFoundException(("Product cost calculation error: Product %s not found. " +
                                "Order %s").formatted(entry.getKey(), order.orderId()));
                    }
                    return product.price().multiply(BigDecimal.valueOf(entry.getValue()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto order) {
        if (order.productPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException(("Total cost calculation error: " +
                    "Order '%s': product price is null").formatted(order.orderId()));
        }

        if (order.deliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException(("Total cost calculation error: " +
                    "Order '%s': delivery price is null").formatted(order.orderId()));
        }

        return order.productPrice()
                .multiply(VAT_MULTIPLIER)
                .add(order.deliveryPrice());
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        UUID orderId = orderClient.getOrderIdByPaymentId(paymentId);
        orderClient.paymentSuccess(orderId);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        UUID orderId = orderClient.getOrderIdByPaymentId(paymentId);
        orderClient.paymentFailed(orderId);
    }
}
