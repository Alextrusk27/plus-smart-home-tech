package ru.practicum.interaction.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.practicum.interaction.api.dto.response.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public record DeliveryRequest(
        @NotNull(message = "From address cannot be null")
        @Valid
        AddressRequest fromAddress,

        @NotNull(message = "To address cannot be null")
        @Valid
        AddressRequest toAddress,

        @NotNull(message = "Order ID cannot be null")
        UUID orderId,

        @NotNull(message = "Delivery weight cannot be null")
        @Positive(message = "Delivery weight must be greater than zero")
        @Digits(integer = 4, fraction = 2, message = "Weight must have up to 4 integer digits and 2 fraction digits")
        BigDecimal deliveryWeight,

        @NotNull(message = "Delivery volume cannot be null")
        @Positive(message = "Delivery volume must be greater than zero")
        @Digits(integer = 4, fraction = 2, message = "Volume must have up to 4 integer digits and 2 fraction digits")
        BigDecimal deliveryVolume,

        @NotNull(message = "Fragile flag cannot be null")
        Boolean fragile
) {
    public static DeliveryRequest of(AddressRequest fromAddress, AddressRequest toAddress, OrderDto order) {
        return new DeliveryRequest(
                fromAddress,
                toAddress,
                order.orderId(),
                order.deliveryWeight(),
                order.deliveryVolume(),
                order.fragile());
    }
}