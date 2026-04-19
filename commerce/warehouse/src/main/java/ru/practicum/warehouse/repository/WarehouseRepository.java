package ru.practicum.warehouse.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.warehouse.model.Product;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Product, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.productId IN :ids")
    List<Product> findAllByIdWithLock(@Param("ids") Set<UUID> ids);

    @Modifying
    @Query("UPDATE Product p SET p.quantity = p.quantity + :quantity WHERE p.productId = :id")
    void increaseQuantity(@Param("id") UUID id, @Param("quantity") int quantity);

    @Modifying
    @Query("UPDATE Product p SET p.quantity = p.quantity - :quantity WHERE p.productId = :id")
    void decreaseQuantity(@Param("id") UUID id, @Param("quantity") int quantity);
}
