package com.watcher.WatcherB.DTO.Order;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateOrderRequest {
    private Integer id; // ID заказа, который нужно обновить
    private Integer statusId;
    private LocalDateTime confirmedAt;
    private LocalDateTime completedAt;
    private LocalDateTime expectedDeliveryAt;
    private String deliveryAddress;
    private String contactPhone;
    private String contactName;
    private List<OrderItemDTO> items;

}
