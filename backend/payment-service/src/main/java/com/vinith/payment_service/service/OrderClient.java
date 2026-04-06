package com.vinith.payment_service.service;

import com.vinith.payment_service.dto.OrderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OrderClient {

    @Value("${order.service.url}")
    private String orderServiceUrl;

    private final WebClient webClient;

    public OrderClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public OrderResponse getOrder(Long orderId) {
        return webClient.get()
                .uri(orderServiceUrl + "/order/v1/orders/" + orderId)
                .retrieve()
                .bodyToMono(OrderResponse.class)
                .block();
    }
}