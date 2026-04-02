package ru.practicum.interaction.api.dto.response;

public record AddressDto(
        String country,
        String city,
        String street,
        String house,
        String flat
) {
}
