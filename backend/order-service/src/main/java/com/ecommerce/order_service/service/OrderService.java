package com.ecommerce.order_service.service;

import com.ecommerce.order_service.client.CatalogClient;
import com.ecommerce.order_service.client.CatalogProductResponse;
import com.ecommerce.order_service.dto.*;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderItem;
import com.ecommerce.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;
import com.ecommerce.order_service.kafka.OrderEventProducer;
import com.ecommerce.order_service.kafka.OrderEvent;
import java.util.UUID;
import java.time.LocalDateTime;
import com.ecommerce.order_service.entity.OrderStatus;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;
    private final OrderEventProducer orderEventProducer;  // add this

    public OrderService(OrderRepository orderRepository,
                        CatalogClient catalogClient,
                        OrderEventProducer orderEventProducer) {  // add this
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
        this.orderEventProducer = orderEventProducer;
    }

    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {

        // Step 1 - Build OrderItems with product snapshot from Catalog
        List<OrderItem> orderItems = request.getItems().stream()
                .map(itemRequest -> {
                    CatalogProductResponse product = catalogClient.getProductById(itemRequest.getProductId());

                    OrderItem item = new OrderItem();
                    item.setProductId(product.getId());
                    item.setProductName(product.getItemName());
                    item.setProductImage(product.getImage());
                    item.setQuantity(itemRequest.getQuantity());
                    item.setPrice(product.getCurrentPrice().doubleValue());

                    return item;
                })
                .toList();

        // Step 2 - Calculate total price
        Double totalPrice = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Step 3 - Build Order entity
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setItems(orderItems);

        // Step 4 - Link each item back to the order
        orderItems.forEach(item -> item.setOrder(order));

        // Step 5 - Save (PrePersist auto sets status + timestamps)
        Order savedOrder = orderRepository.save(order);

        // Step 6 - Publish Kafka event
        OrderEvent event = new OrderEvent(
                UUID.randomUUID().toString(),
                "ORDER_CREATED",
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getCreatedAt().toString()
        );
        orderEventProducer.publishOrderEvent(event);

        // Step 7 - Map to response DTO and return
        return mapToOrderResponse(savedOrder);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productImage(item.getProductImage())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .toList();

        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }
    // Get all orders for a user
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    // Get single order by ID
    public OrderResponse getOrderByIdAndUserId(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        return mapToOrderResponse(order);
    }

    // Cancel order
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new RuntimeException("Cannot cancel a paid order");
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);

        OrderEvent event = new OrderEvent(
                UUID.randomUUID().toString(),
                "ORDER_CANCELLED",
                saved.getId(),
                saved.getUserId(),
                saved.getUpdatedAt() != null ? saved.getUpdatedAt().toString() : LocalDateTime.now().toString()
        );
        orderEventProducer.publishOrderEvent(event);

        return mapToOrderResponse(saved);
    }
}