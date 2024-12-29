package com.watcher.WatcherB.controllers;

import com.watcher.WatcherB.DTO.ErrorResponse;
import com.watcher.WatcherB.DTO.Order.*;
import com.watcher.WatcherB.DTO.PagedResponse;
import com.watcher.WatcherB.DTO.Product.*;
import com.watcher.WatcherB.services.OrderService;
import com.watcher.WatcherB.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    private final OrderService orderService;
    OrdersController(@Autowired OrderService orderService) {
        this.orderService = orderService;
    }

    @Secured({"ROLE_OPERATOR", "ROLE_ADMIN"})
    @GetMapping("/catalog")
    public ResponseEntity<?> getCatalog(
            @ModelAttribute OrderListRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size){
        if(page < 0 || size < 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Неверные размеры страницы"));
        }
        Pageable pageable = PageRequest.of(page, Math.min(size,30));
        PagedResponse<OrderResponse> pagedResponse = orderService.getOrders(request, pageable);
        return ResponseEntity.ok(pagedResponse);
    }

    @GetMapping("/order")
    public ResponseEntity<?> getOrder(@ModelAttribute OrderRequest request){
        try {
            return ResponseEntity.ok(orderService.getOrder(request));
        }catch (OrderService.OrderNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @Secured({"ROLE_OPERATOR", "ROLE_ADMIN"})
    @DeleteMapping("/order")
    public ResponseEntity<?> deleteOrder(@ModelAttribute OrderRequest request){
        try{
            orderService.deleteOrder(request);
            return ResponseEntity.ok().body("Продукт успешно удален");
        }catch (ProductService.ProductNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }
    @Secured({"ROLE_OPERATOR", "ROLE_ADMIN"})
    @PatchMapping("/order")
    public ResponseEntity<?> updateOrder(@RequestBody UpdateOrderRequest request){
        try{
            return ResponseEntity.ok(orderService.updateOrder(request));
        }catch (ProductService.ProductNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/order")
    public ResponseEntity<?> addOrder(@RequestBody CreateOrderRequest request){
        try{
            return ResponseEntity.ok(orderService.createOrder(request));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }
}