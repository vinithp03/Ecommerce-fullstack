package com.vinith.payment_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentGatewayClient {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentGatewayClient.class);

    @Retry(name = "paymentGateway")
    @CircuitBreaker(
            name = "paymentGateway",
            fallbackMethod = "gatewayFallback"
    )
    public boolean processPayment(Long orderId, BigDecimal amount) {

        log.info("[GATEWAY] Calling payment gateway for orderId: {}", orderId);

        boolean success = Math.random() > 0.1;

        if (!success) {
            throw new RuntimeException(
                    "Payment gateway declined for orderId: " + orderId
            );
        }

        return true;
    }

    public boolean gatewayFallback(
            Long orderId,
            BigDecimal amount,
            Throwable ex
    ) {
        log.warn(
                "[GATEWAY] Fallback triggered for orderId: {} Cause: {}",
                orderId,
                ex.getMessage()
        );

        return false;
    }
}