package com.ecommerce.order_service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private String productImage;
    private Integer quantity;
    private Double price;
}