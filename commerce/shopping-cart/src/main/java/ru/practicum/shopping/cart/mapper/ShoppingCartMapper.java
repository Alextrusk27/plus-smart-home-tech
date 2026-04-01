package ru.practicum.shopping.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.interaction.api.dto.ShoppingCartDto;
import ru.practicum.shopping.cart.dto.AddToCartRequest;
import ru.practicum.shopping.cart.model.Cart;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    @Mapping(target = "shoppingCartId", ignore = true)
    Cart toEntity(AddToCartRequest request);

    ShoppingCartDto toDto(Cart cart);
}
