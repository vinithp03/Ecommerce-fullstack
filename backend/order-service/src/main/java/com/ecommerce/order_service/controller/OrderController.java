package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.CreateOrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/health")
    public String health() {
        return "Order service running";
    }

    // Create order
    @PostMapping("/v1/users/{userId}/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @PathVariable Long userId,
            @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(userId, request));
    }

    // Get all orders for a user
    @GetMapping("/v1/users/{userId}/orders")
    public ResponseEntity<List<OrderResponse>> getOrders(
            @PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    // Get single order
    @GetMapping("/v1/users/{userId}/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long userId,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderByIdAndUserId(orderId, userId));
    }

    // Cancel order
    @PatchMapping("/v1/users/{userId}/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long userId,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId, userId));
    }
}