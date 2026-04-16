package ru.practicum.interaction.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler({
            CartNotFoundException.class,
            NoProductsInShoppingCartException.class,
            ProductNotFoundException.class,
            NoOrderFoundException.class,
    })
    public ResponseEntity<ApiError> handleException(RuntimeException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        log.error("Not found: {}", e.getMessage());
        return ResponseEntity.status(status)
                .body(ApiError.of(status.name(), e.getMessage(), status.value()));
    }

    @ExceptionHandler({
            SpecifiedProductAlreadyInWarehouseException.class,
            ProductInShoppingCartLowQuantityInWarehouse.class
    })
    public ResponseEntity<ApiError> handleConflict(RuntimeException e) {
        HttpStatus status = HttpStatus.CONFLICT;
        log.error("Conflict: {}", e.getMessage());
        return ResponseEntity.status(status)
                .body(ApiError.of(status.name(), e.getMessage(), status.value()));
    }

    @ExceptionHandler(ProductCartException.class)
    public ResponseEntity<ApiError> handleProductCartException(ProductCartException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.error("Invalid cart request: {}", e.getMessage());
        return ResponseEntity.status(status)
                .body(ApiError.of(status.name(), e.getMessage(), status.value()));
    }

    @ExceptionHandler(ProductCartException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(NotAuthorizedUserException e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        log.error("Authorization error: {}", e.getMessage());
        return ResponseEntity.status(status)
                .body(ApiError.of(status.name(), e.getMessage(), status.value()));
    }
}
