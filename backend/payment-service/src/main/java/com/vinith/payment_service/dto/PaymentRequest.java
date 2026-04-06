package com.vinith.payment_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
}