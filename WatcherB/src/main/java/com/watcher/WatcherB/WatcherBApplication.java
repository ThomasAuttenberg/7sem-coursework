package com.watcher.WatcherB;

import com.watcher.WatcherB.DTO.Order.CreateOrderRequest;
import com.watcher.WatcherB.DTO.Order.OrderItemDTO;
import com.watcher.WatcherB.DTO.Order.OrderResponse;
import com.watcher.WatcherB.models.Data.Goods.Product;
import com.watcher.WatcherB.models.Data.Order.Order;
import com.watcher.WatcherB.repositories.OrderRepository;
import com.watcher.WatcherB.repositories.ProductRepository;
import com.watcher.WatcherB.services.OrderService;
import com.watcher.WatcherB.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class WatcherBApplication implements CommandLineRunner {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductService productService;
	@Autowired
	private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

	public static void main(String[] args) {
		SpringApplication.run(WatcherBApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		int page = 0;
		Pageable pageable = PageRequest.of(page, Math.min(20, 30));
		Page<Order> products = orderRepository.findWithFilters(
				null,
				null,
				null,
				null,
				null,
				pageable
		);
		//OrderResponse orderResponse = orderService.getOrder(12);
		//System.out.println(orderResponse);
//		products.getContent().forEach(System.out::println);
//		//orderService.deleteOrder(1);
//		CreateOrderRequest createOrderRequest = new CreateOrderRequest();
//		createOrderRequest.setContactName("Макс");
//		createOrderRequest.setContactPhone("1888888888");
//		createOrderRequest.setDeliveryAddress("Садовая");
//		ArrayList<OrderItemDTO> orderItems = new ArrayList<>();
//		orderItems.add(OrderItemDTO.builder().productId(2).quantity(1).build());
//		createOrderRequest.setItems(orderItems);
//		orderService.createOrder(createOrderRequest);
//		System.out.println("Произошло Создание");
//		products = orderRepository.findWithFilters(
//				null,
//				null,
//				null,
//				null,
//				null,
//				null,
//				pageable
//		);
//		products.getContent().forEach(System.out::println);
//
	}
}
