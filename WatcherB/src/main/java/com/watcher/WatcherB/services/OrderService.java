package com.watcher.WatcherB.services;

import com.watcher.WatcherB.DTO.Order.*;
import com.watcher.WatcherB.DTO.PagedResponse;
import com.watcher.WatcherB.models.Data.Goods.Product;
import com.watcher.WatcherB.models.Data.Order.Order;
import com.watcher.WatcherB.models.Data.Order.OrderItem;
import com.watcher.WatcherB.models.Data.Order.OrderStatus;
import com.watcher.WatcherB.repositories.OrderRepository;
import com.watcher.WatcherB.repositories.OrderStatusRepository;
import com.watcher.WatcherB.repositories.ProductRepository;
import com.watcher.WatcherB.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderStatusRepository orderStatusRepository;

    public OrderService(@Autowired OrderRepository orderRepository,
                        @Autowired OrderStatusRepository orderStatusRepository,
                        @Autowired ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderStatusRepository = orderStatusRepository;
    }
    @Transactional
    public OrderResponse getOrder(OrderRequest request){
        return orderRepository.findByAccessHash(request.getHash())
                .map(OrderResponse::new)
                .orElseThrow(() -> new OrderNotFoundException("Заказ с заданным ID не найден"));
    }

    public PagedResponse<OrderResponse> getOrders(OrderListRequest request, Pageable pageable) {
        return new PagedResponse<>(orderRepository.findWithFilters(
                request.getDeliveryAddress(),
                request.getCreatedDate(),
                request.getExpectedDeliveryDate(),
                request.getContactPhone(),
                request.getContactName(),
                pageable
        ).map(OrderResponse::new));
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // Проверяем обязательные поля
        ValidationUtils.requireNonNull(request.getDeliveryAddress(), "Delivery address is required");
        ValidationUtils.requireNonNull(request.getContactPhone(), "Contact phone is required");
        ValidationUtils.requireNonNull(request.getContactName(), "Contact name is required");
        ValidationUtils.requireNonEmpty(request.getItems(), "Order items are required");

        // Проверяем наличие статуса заказа
        OrderStatus status = orderStatusRepository.findByStatus("Создан")
                .orElseThrow(() -> new IllegalArgumentException("Статус 'Создан' не найден в БД"));

        // Создаём новый заказ
        Order order = new Order();
        order.setStatus(status);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setContactPhone(request.getContactPhone());
        order.setContactName(request.getContactName());
        order.setCreatedAt(LocalDateTime.now());

        // Сохраняем заказ, чтобы получить ID
        order = orderRepository.save(order);

        // Обрабатываем элементы заказа
        List<OrderItem> items = new ArrayList<>();
        for(OrderItemDTO item : request.getItems()){
            ValidationUtils.requireNonNull(item.getProductId(), "Идентификатор товара не указан");
            ValidationUtils.requirePositive(item.getQuantity(), "Количество должно быть положительно определено");

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(()-> new IllegalArgumentException("Продукт с указанным идентификатором не найден"));

            OrderItem orderItem = new OrderItem();
            OrderItem.OrderItemId orderItemId = new OrderItem.OrderItemId();
            orderItemId.setProductId(product.getId());
            orderItemId.setOrderId(order.getId());
            orderItem.setId(orderItemId);
            orderItem.setProduct(product);
            orderItem.setOrder(order);
            orderItem.setQuantity(item.getQuantity());
            items.add(orderItem);
        }
        // Устанавливаем элементы в заказ
        order.setItems(items);

        // Сохраняем обновлённый заказ
        order = orderRepository.save(order);

        return new OrderResponse(order);
    }

    @Transactional
    public OrderResponse updateOrder(UpdateOrderRequest request) {
        // Проверяем наличие заказа
        if(request.getId() == null){
            throw new IllegalArgumentException("ID заказа не установлен");
        }
        Order order = orderRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Заказ с ID " + request.getId() + " не найден"));

        // Обновляем поля, если они указаны
        if (request.getStatusId() != null) {
            OrderStatus status = orderStatusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("Статус с ID " + request.getStatusId() + " не найден"));
            order.setStatus(status);
        }
        if (request.getConfirmedAt() != null) order.setConfirmedAt(request.getConfirmedAt());
        if (request.getCompletedAt() != null) order.setCompletedAt(request.getCompletedAt());
        if (request.getExpectedDeliveryAt() != null) order.setExpectedDeliveryAt(request.getExpectedDeliveryAt());
        if (request.getDeliveryAddress() != null) order.setDeliveryAddress(request.getDeliveryAddress());
        if (request.getContactPhone() != null) order.setContactPhone(request.getContactPhone());
        if (request.getContactName() != null) order.setContactName(request.getContactName());

        // Обновляем элементы заказа, если они указаны
        if (request.getItems() != null) {
            List<OrderItem> items = new ArrayList<>();
            for(OrderItemDTO item : request.getItems()){
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Продукт с ID " + item.getProductId() + " не найден"));
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);

                orderItem.setQuantity(item.getQuantity());
                items.add(orderItem);
            }

            order.getItems().clear();
            order.getItems().addAll(items);
        }

        // Сохраняем изменения
        order = orderRepository.save(order);

        return new OrderResponse(order);
    }

    @Transactional
    public void deleteOrder(OrderRequest request) {
        if (!orderRepository.existsByAccessHash(request.getHash())) {
            throw new IllegalArgumentException("Заказ с идентификатором " + request.getHash() + " не найден");
        }
        orderRepository.deleteByAccessHash(request.getHash());
    }

    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String s) {
        }
    }

}
