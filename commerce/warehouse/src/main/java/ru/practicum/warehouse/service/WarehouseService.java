package ru.practicum.warehouse.service;

import ru.practicum.interaction.api.dto.request.AddProductToWarehouseRequest;
import ru.practicum.interaction.api.dto.request.NewProductInWarehouseRequest;
import ru.practicum.interaction.api.dto.response.AddressDto;
import ru.practicum.interaction.api.dto.response.BookedProductsDto;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;

public interface WarehouseService {

    void createProduct(NewProductInWarehouseRequest request);

    BookedProductsDto checkProduct(ShoppingCartDto shoppingCart);

    void addProduct(AddProductToWarehouseRequest request);

    AddressDto getAddress();
}
