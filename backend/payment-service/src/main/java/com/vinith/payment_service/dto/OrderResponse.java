package com.vinith.payment_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderResponse {
    @JsonProperty("orderId")
    private Long id;
    private Long userId;
    @JsonProperty("totalPrice")
    private BigDecimal totalAmount;
    private String status;
}