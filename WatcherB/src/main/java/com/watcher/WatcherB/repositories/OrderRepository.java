package com.watcher.WatcherB.repositories;

import com.watcher.WatcherB.models.Data.Order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o " +
            "WHERE" +
            "(:deliveryAddress IS NULL OR o.deliveryAddress LIKE %:deliveryAddress%) " +
            "AND (cast(:deliveryDate as timestamp) IS NULL  OR o.expectedDeliveryAt = :deliveryDate) " +
            "AND (cast(:createdDate as timestamp) IS NULL OR o.createdAt = :createdDate) " +
            "AND (:contactPhone IS NULL OR o.contactPhone LIKE %:contactPhone%) " +
            "AND (:customerName IS NULL OR o.contactName LIKE %:customerName%)")
    Page<Order> findWithFilters(
            @Param("deliveryAddress") String deliveryAddress,
            @Param("createdDate") LocalDateTime createdDate,
            @Param("deliveryDate") LocalDateTime deliveryDate,
            @Param("contactPhone") String contactPhone,
            @Param("customerName") String customerName,
            Pageable pageable);

    Optional<Order> findByAccessHash(String hash);

    boolean existsByAccessHash(String hash);

    void deleteByAccessHash(String hash);
}
