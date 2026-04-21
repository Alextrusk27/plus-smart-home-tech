package ru.practicum.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.interaction.api.dto.request.AssemblyProductsForOrderRequest;
import ru.practicum.warehouse.model.OrderBooking;

@Mapper(componentModel = "spring")
public interface OrderBookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deliveryId", ignore = true)
    OrderBooking toEntity(AssemblyProductsForOrderRequest request);
}
