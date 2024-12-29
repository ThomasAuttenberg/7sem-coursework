package com.watcher.WatcherB.DTO.Order;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderListRequest {
    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime createdDate;
    private LocalDateTime completedDate;
    private String contactPhone;
    private String contactName;
    private String deliveryAddress;
}
