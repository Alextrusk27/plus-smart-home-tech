package ru.practicum.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.interaction.api.dto.request.NewProductInWarehouseRequest;
import ru.practicum.warehouse.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "quantity", ignore = true)
    Product toEntity(NewProductInWarehouseRequest request);
}
