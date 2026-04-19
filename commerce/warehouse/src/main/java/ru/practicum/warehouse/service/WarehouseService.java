package ru.practicum.warehouse.service;

import ru.practicum.interaction.api.dto.request.AddProductToWarehouseRequest;
import ru.practicum.interaction.api.dto.request.AssemblyProductsForOrderRequest;
import ru.practicum.interaction.api.dto.request.NewProductInWarehouseRequest;
import ru.practicum.interaction.api.dto.request.ShippedToDeliveryRequest;
import ru.practicum.interaction.api.dto.response.AddressDto;
import ru.practicum.interaction.api.dto.response.BookedProductsDto;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {

    void createProduct(NewProductInWarehouseRequest request);

    BookedProductsDto checkProduct(ShoppingCartDto shoppingCart);

    void shippedToDelivery(ShippedToDeliveryRequest request);

    void addProduct(AddProductToWarehouseRequest request);

    void returnProducts(Map<UUID, Integer> products);

    BookedProductsDto assemblyForOrder(AssemblyProductsForOrderRequest request);

    AddressDto getAddress();
}
