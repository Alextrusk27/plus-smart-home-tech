package ru.practicum.shopping.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.interaction.api.dto.request.AddToCartRequest;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;
import ru.practicum.shopping.cart.model.Cart;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    @Mapping(target = "shoppingCartId", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Cart toEntity(AddToCartRequest request);

    ShoppingCartDto toDto(Cart cart);
}
