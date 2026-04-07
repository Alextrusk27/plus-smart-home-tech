package ru.practicum.interaction.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.interaction.api.api.WarehouseApi;
import ru.practicum.interaction.api.dto.request.AddProductToWarehouseRequest;
import ru.practicum.interaction.api.dto.request.NewProductInWarehouseRequest;
import ru.practicum.interaction.api.dto.response.AddressDto;
import ru.practicum.interaction.api.dto.response.BookedProductsDto;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient extends WarehouseApi {

    @Override
    @PutMapping
    void createProduct(@RequestBody NewProductInWarehouseRequest request);

    @Override
    @PostMapping("/check")
    BookedProductsDto checkProduct(@RequestBody ShoppingCartDto shoppingCart);

    @Override
    @PostMapping("/add")
    void addProduct(@RequestBody AddProductToWarehouseRequest request);

    @Override
    @GetMapping("/address")
    AddressDto getAddress();
}
