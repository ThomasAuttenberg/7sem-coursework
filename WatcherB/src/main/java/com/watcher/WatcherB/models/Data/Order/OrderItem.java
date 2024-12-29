package com.watcher.WatcherB.models.Data.Order;

import com.watcher.WatcherB.models.Data.Goods.Product;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table (name = "order_items")
public class OrderItem {

    @EmbeddedId
    private OrderItemId id;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;;

    @Embeddable
    @Data
    @NoArgsConstructor
    public static class OrderItemId implements Serializable {
        private Integer orderId;
        private Integer productId;
    }
}
