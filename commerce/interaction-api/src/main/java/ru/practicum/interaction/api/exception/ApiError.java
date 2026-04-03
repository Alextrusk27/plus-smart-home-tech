package ru.practicum.interaction.api.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ApiError(
    String error,
    String message,
    int status,
    String timestamp
) {
    public static ApiError of(String error, String message, int status) {
        return new ApiError(
                error,
                message,
                status,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
