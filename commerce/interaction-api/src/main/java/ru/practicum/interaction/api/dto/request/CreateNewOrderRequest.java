package ru.practicum.interaction.api.dto.request;

import jakarta.validation.constraints.NotNull;
import ru.practicum.interaction.api.dto.response.AddressDto;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;

public record CreateNewOrderRequest(
        @NotNull
        ShoppingCartDto shoppingCart,

        @NotNull
        AddressDto deliveryAddress
) {
}
