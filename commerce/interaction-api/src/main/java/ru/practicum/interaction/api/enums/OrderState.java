package ru.practicum.interaction.api.enums;

public enum OrderState {
    NEW,
    ON_PAYMENT,
    ON_DELIVERY,
    ON_ASSEMBLY,
    DELIVERED,
    ASSEMBLED,
    PAID,
    COMPLETED,
    DELIVERY_FAILED,
    ASSEMBLY_FAILED,
    PAYMENT_FAILED,
    PRODUCT_RETURNED,
    CANCELED
}