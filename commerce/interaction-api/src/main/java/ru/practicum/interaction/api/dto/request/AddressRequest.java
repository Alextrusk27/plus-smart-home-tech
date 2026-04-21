package ru.practicum.interaction.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.practicum.interaction.api.dto.response.AddressDto;

public record AddressRequest(
        @NotBlank(message = "Country is required")
        @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
        String country,

        @NotBlank(message = "City is required")
        @Size(min = 3, max = 100, message = "City must be between 3 and 100 characters")
        String city,

        @NotBlank(message = "Street is required")
        @Size(min = 3, max = 200, message = "Street must be between 3 and 200 characters")
        String street,

        @NotBlank(message = "House is required")
        @Size(min = 1, max = 20, message = "House must be between 1 and 20 characters")
        String house,

        @Size(min = 1, max = 20, message = "Flat must be between 1 and 20 characters")
        String flat
) {
    public static AddressRequest of(AddressDto dto) {
        return new AddressRequest(dto.country(), dto.city(), dto.street(), dto.house(), dto.flat());
    }
}
