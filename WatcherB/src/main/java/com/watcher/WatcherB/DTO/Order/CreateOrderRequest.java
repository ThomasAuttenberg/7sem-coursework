package com.watcher.WatcherB.DTO.Order;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private String deliveryAddress;
    private String contactPhone;
    private String contactName;
    private List<OrderItemDTO> items;

}