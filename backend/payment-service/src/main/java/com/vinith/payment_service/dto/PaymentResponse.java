package com.vinith.payment_service.dto;

import com.vinith.payment_service.entity.PaymentStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String paymentMethod;
    private String failureReason;
    private LocalDateTime createdAt;
}