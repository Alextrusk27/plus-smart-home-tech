package ru.practicum.interaction.api.exception;

public class NoOrderBookingFoundException extends RuntimeException {
    public NoOrderBookingFoundException(String message) {
        super(message);
    }
}
