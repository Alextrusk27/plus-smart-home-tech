package ru.practicum.shopping.store.mapper;

import org.mapstruct.*;
import ru.practicum.interaction.api.dto.request.CreateProductRequest;
import ru.practicum.interaction.api.dto.request.UpdateProductRequest;
import ru.practicum.interaction.api.dto.response.ProductDto;
import ru.practicum.shopping.store.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toDto(Product product);

    @Mapping(target = "productId", ignore = true)
    Product toEntity(CreateProductRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Product product, UpdateProductRequest request);
}
