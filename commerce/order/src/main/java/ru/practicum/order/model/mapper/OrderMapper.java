package ru.practicum.order.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import ru.practicum.interaction.api.dto.request.CreateNewOrderRequest;
import ru.practicum.interaction.api.dto.response.OrderDto;
import ru.practicum.order.model.Order;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mappings({
            @Mapping(target = "shoppingCartId", source = "request.shoppingCart.shoppingCartId"),
            @Mapping(target = "products", source = "request.shoppingCart.products"),
    })
    Order toEntity(CreateNewOrderRequest request);

    OrderDto toDto(Order order);
}
