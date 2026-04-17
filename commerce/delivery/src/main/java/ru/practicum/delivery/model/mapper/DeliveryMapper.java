package ru.practicum.delivery.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.delivery.model.Delivery;
import ru.practicum.interaction.api.dto.request.DeliveryRequest;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface DeliveryMapper {

    @Mapping(target = "deliveryId", ignore = true)
    @Mapping(target = "state", ignore = true)
    Delivery toEntity(DeliveryRequest request);
}
