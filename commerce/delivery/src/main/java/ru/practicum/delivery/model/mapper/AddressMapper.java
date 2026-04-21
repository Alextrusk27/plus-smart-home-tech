package ru.practicum.delivery.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.delivery.model.Address;
import ru.practicum.interaction.api.dto.request.AddressRequest;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    Address toEntity(AddressRequest request);
}
