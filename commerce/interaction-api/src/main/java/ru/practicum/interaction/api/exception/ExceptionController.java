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
            NoDeliveryFoundException.class,
    })
    public ResponseEntity<ApiError> handleNotFoundException(RuntimeException e) {
        log.warn("Not found: {}", e.getMessage());  // WARN, не ERROR
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("NOT_FOUND", e.getMessage(), 404));
    }

    @ExceptionHandler({
            SpecifiedProductAlreadyInWarehouseException.class,
            ProductInShoppingCartLowQuantityInWarehouse.class
    })
    public ResponseEntity<ApiError> handleConflictException(RuntimeException e) {
        log.warn("Conflict: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("CONFLICT", e.getMessage(), 409));
    }

    @ExceptionHandler(ProductCartException.class)
    public ResponseEntity<ApiError> handleProductCartException(ProductCartException e) {
        log.warn("Invalid cart request: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of("BAD_REQUEST", e.getMessage(), 400));
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(NotAuthorizedUserException e) {
        log.warn("Authorization error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.of("UNAUTHORIZED", e.getMessage(), 401));
    }

    @ExceptionHandler(NotEnoughInfoInOrderToCalculateException.class)
    public ResponseEntity<ApiError> handleNotEnoughInfoException(NotEnoughInfoInOrderToCalculateException e) {
        log.warn("Not enough info: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiError.of("UNPROCESSABLE_ENTITY", e.getMessage(), 422));
    }

    @ExceptionHandler(OrderCreationFailedException.class)
    public ResponseEntity<ApiError> handleOrderCreationFailed(OrderCreationFailedException e) {
        log.error("Order creation failed", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of("ORDER_CREATION_FAILED", e.getMessage(), 500));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception e) {
        log.error("Unexpected server error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of("INTERNAL_ERROR", "An unexpected error occurred", 500));
    }
}
