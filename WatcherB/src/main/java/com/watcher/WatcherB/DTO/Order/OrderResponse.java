package com.watcher.WatcherB.DTO.Order;

import com.watcher.WatcherB.models.Data.Order.Order;
import com.watcher.WatcherB.models.Data.Order.OrderItem;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
public class OrderResponse {
    private final Integer id;
    private final String status;
    private final List<OrderItemDTO> items;
    private final LocalDateTime createdAt;
    private final LocalDateTime confirmedAt;
    private final LocalDateTime completedAt;
    private final LocalDateTime expectedDeliveryAt;
    private final String deliveryAddress;
    private final String contactPhone;
    private final String accessHash;
    public OrderResponse(Order order) {
        this.id = order.getId();
        this.status = order.getStatus().getStatus();
        this.createdAt = order.getCreatedAt();
        this.confirmedAt = order.getConfirmedAt();
        this.completedAt = order.getCompletedAt();
        this.expectedDeliveryAt = order.getExpectedDeliveryAt();
        this.deliveryAddress = order.getDeliveryAddress();
        this.contactPhone = order.getContactPhone();
        this.accessHash = order.getAccessHash();
        items = new LinkedList<>();
        for(OrderItem item : order.getItems()) {
            items.add(new OrderItemDTO(item));
        }
    }
}
