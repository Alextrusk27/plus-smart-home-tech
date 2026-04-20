package ru.practicum.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.delivery.model.Address;
import ru.practicum.interaction.api.dto.request.AddressRequest;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    Optional<Address> findByCountryAndCityAndStreetAndHouseAndFlat(
            String country, String city, String street, String house, String flat
    );

    default Optional<Address> findByRequest(AddressRequest req) {
        return findByCountryAndCityAndStreetAndHouseAndFlat(
                req.country(),
                req.city(),
                req.street(),
                req.house(),
                req.flat()
        );
    }
}
