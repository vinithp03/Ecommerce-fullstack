package com.ecommerce.order_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderEvent {
    private String eventId;
    private String eventType;   // ORDER_CREATED, ORDER_CANCELLED
    private Long orderId;
    private Long userId;
    private String timestamp;
}