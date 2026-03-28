package com.ecommerce.order_service.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
    private List<OrderItemRequest> items;
}