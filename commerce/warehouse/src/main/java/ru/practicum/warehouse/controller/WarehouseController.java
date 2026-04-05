package ru.practicum.warehouse.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.dto.request.AddProductToWarehouseRequest;
import ru.practicum.interaction.api.dto.request.NewProductInWarehouseRequest;
import ru.practicum.interaction.api.dto.response.AddressDto;
import ru.practicum.interaction.api.dto.response.BookedProductsDto;
import ru.practicum.interaction.api.dto.response.ShoppingCartDto;
import ru.practicum.warehouse.service.WarehouseService;

@RestController
@RequestMapping("/api/v1/warehouse")
@AllArgsConstructor
@Slf4j
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody @Valid NewProductInWarehouseRequest request) {

        log.info("Adding to warehouse new product '{}'", request.productId());
        warehouseService.createProduct(request);
        log.debug("Product '{}' successfully added", request.productId());
    }

    @PostMapping("/check")
    public BookedProductsDto checkProduct(@RequestBody ShoppingCartDto shoppingCart) {

        log.info("Checking availability for {} products", shoppingCart.products().size());
        var result = warehouseService.checkProduct(shoppingCart);
        log.debug("All products available");
        return result;
    }

    @PostMapping("/add")
    public void addProduct(@RequestBody @Valid AddProductToWarehouseRequest request) {

        log.info("Changing quantity for product '{}' to '{}'", request.productId(),
                request.quantity());
        warehouseService.addProduct(request);
        log.debug("Product '{}' quantity successfully changed", request.productId());
    }

    @GetMapping("/address")
    public AddressDto getAddress() {

        log.info("Getting warehouse address");
        var result = warehouseService.getAddress();
        log.debug("Warehouse address successfully retrieved");
        return result;
    }
}
