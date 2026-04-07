package ru.practicum.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.warehouse.model.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
