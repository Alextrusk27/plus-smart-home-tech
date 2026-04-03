package ru.practicum.interaction.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({
            CartNotFoundException.class,
            NoProductsInShoppingCartException.class,
            ProductNotFoundException.class
    })
    public ResponseEntity<ApiError> handleException(RuntimeException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        log.error("Not found: {}", e.getMessage(), e);
        return ResponseEntity.status(status)
                .body(ApiError.of(status.name(), e.getMessage(), status.value()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            SpecifiedProductAlreadyInWarehouseException.class,
            ProductInShoppingCartLowQuantityInWarehouse.class
    })
    public ResponseEntity<ApiError> handleConflict(RuntimeException e) {
        HttpStatus status = HttpStatus.CONFLICT;
        log.error("Conflict: {}", e.getMessage(), e);
        return ResponseEntity.status(status)
                .body(ApiError.of(status.name(), e.getMessage(), status.value()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ProductCartException.class)
    public ResponseEntity<ApiError> handleProductCartException(ProductCartException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.error("Invalid cart request: {}", e.getMessage(), e);
        return ResponseEntity.status(status)
                .body(ApiError.of(status.name(), e.getMessage(), status.value()));
    }
}
