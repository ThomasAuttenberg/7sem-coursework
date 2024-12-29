package com.watcher.WatcherB.repositories;

import com.watcher.WatcherB.models.Data.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {
    Optional<OrderStatus> findByStatus(String status);
}
