package ru.practicum.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.order.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findAllByUsername(String username, Pageable pageable);

    @Query("SELECT o.orderId FROM Order o WHERE o.paymentId = :paymentId")
    Optional<UUID> findOrderIdByPaymentId(@Param("paymentId") UUID paymentId);
}
