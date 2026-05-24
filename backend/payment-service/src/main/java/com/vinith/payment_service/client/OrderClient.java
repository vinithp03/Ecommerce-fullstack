package com.vinith.payment_service.client;

import com.vinith.payment_service.dto.OrderResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OrderClient {

    private static final Logger log =
            LoggerFactory.getLogger(OrderClient.class);

    private final WebClient webClient;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    public OrderClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Retry(name = "orderService")
    @CircuitBreaker(
            name = "orderService",
            fallbackMethod = "orderServiceFallback"
    )
    public OrderResponse getOrder(Long orderId) {

        log.info("Calling Order Service for orderId: {}", orderId);

        return webClient.get()
                .uri(orderServiceUrl + "/order/v1/orders/" + orderId)
                .retrieve()
                .bodyToMono(OrderResponse.class)
                .block();
    }

    public OrderResponse orderServiceFallback(Long orderId, Throwable ex) {

        log.error("Order service down for orderId: {}", orderId);

        OrderResponse fallback = new OrderResponse();
        fallback.setId(orderId);
        fallback.setStatus("UNKNOWN");
        fallback.setServiceDown(true);

        return fallback;
    }
}