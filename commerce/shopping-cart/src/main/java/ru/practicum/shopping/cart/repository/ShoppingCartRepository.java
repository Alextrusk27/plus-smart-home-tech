package ru.practicum.shopping.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shopping.cart.model.Cart;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByUsernameAndIsActiveTrue(String username);
}
