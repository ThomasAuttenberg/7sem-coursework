package com.watcher.WatcherB.models.Data.Order;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table (name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    private OrderStatus status;

    @ToString.Exclude
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime confirmedAt;
    private LocalDateTime completedAt;
    private LocalDateTime expectedDeliveryAt;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private String contactPhone;

    @Column(nullable = false)
    private String contactName;

    @Column(nullable = false, unique = true)
    private String accessHash;

    @PrePersist
    private void generateAccessHash() {
        try {
            // Генерируем случайный UUID
            String randomSalt = UUID.randomUUID().toString();

            // Комбинируем данные заказа с солью
            String data = randomSalt + contactName + contactPhone + createdAt.toString();

            // Создаём SHA-256 хэш
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            // Конвертируем хэш в строку в формате Base64
            this.accessHash = Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }
}
