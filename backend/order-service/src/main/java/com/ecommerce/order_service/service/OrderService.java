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
    private final OrderEventProducer orderEventProducer;

    public OrderService(OrderRepository orderRepository,
                        CatalogClient catalogClient,
                        OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
        this.orderEventProducer = orderEventProducer;
    }

    // ✅ CREATE ORDER
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {

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

        Double totalPrice = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setItems(orderItems);

        orderItems.forEach(item -> item.setOrder(order));

        Order savedOrder = orderRepository.save(order);

        // Kafka event
        OrderEvent event = new OrderEvent(
                UUID.randomUUID().toString(),
                "ORDER_CREATED",
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getCreatedAt().toString()
        );
        orderEventProducer.publishOrderEvent(event);

        return mapToOrderResponse(savedOrder);
    }

    // ✅ NEW (for Payment Service)
    public OrderResponse getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::mapToOrderResponse) // ✅ FIXED HERE
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // ✅ GET ALL ORDERS (USER)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    // ✅ GET SINGLE ORDER (USER)
    public OrderResponse getOrderByIdAndUserId(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        return mapToOrderResponse(order);
    }

    // ✅ CANCEL ORDER
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
                saved.getUpdatedAt() != null
                        ? saved.getUpdatedAt().toString()
                        : LocalDateTime.now().toString()
        );
        orderEventProducer.publishOrderEvent(event);

        return mapToOrderResponse(saved);
    }

    // ✅ COMMON MAPPER (USED EVERYWHERE)
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
}