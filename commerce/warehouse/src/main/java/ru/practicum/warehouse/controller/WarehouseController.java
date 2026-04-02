package ru.practicum.warehouse.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.dto.request.ChangeQuantity;
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
    public ResponseEntity<Void> createProduct(@RequestBody @Valid NewProductInWarehouseRequest request) {

        log.info("Adding to warehouse new product '{}'", request.productId());
        warehouseService.createProduct(request);
        log.debug("Product '{}' successfully added", request.productId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/check")
    public ResponseEntity<BookedProductsDto> checkProduct(@RequestBody ShoppingCartDto shoppingCart) {

        log.info("Checking availability for {} products", shoppingCart.products().size());
        var result = warehouseService.checkProduct(shoppingCart);
        log.debug("All products available");
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addProduct(@RequestBody @Valid ChangeQuantity changeQuantity) {

        log.info("Changing quantity for product '{}' to '{}'", changeQuantity.productId(),
                changeQuantity.newQuantity());
        warehouseService.addProduct(changeQuantity);
        log.debug("Product '{}' quantity successfully changed", changeQuantity.productId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/address")
    public ResponseEntity<AddressDto> getAddress() {

        log.info("Getting warehouse address");
        var result = warehouseService.getAddress();
        log.debug("Warehouse address successfully retrieved");
        return ResponseEntity.ok().body(result);
    }
}
