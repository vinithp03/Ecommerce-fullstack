package com.ecommerce.order_service.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private Double totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}