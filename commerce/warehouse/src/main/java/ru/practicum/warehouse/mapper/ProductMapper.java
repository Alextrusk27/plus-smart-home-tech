package ru.practicum.warehouse.mapper;

import org.mapstruct.Mapper;
import ru.practicum.interaction.api.dto.request.NewProductInWarehouseRequest;
import ru.practicum.warehouse.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(NewProductInWarehouseRequest request);
}
