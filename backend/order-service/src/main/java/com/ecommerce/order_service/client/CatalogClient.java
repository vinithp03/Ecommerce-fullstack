package com.ecommerce.order_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CatalogClient {

    private final WebClient webClient;

    @Value("${catalog.service.url}")
    private String catalogServiceUrl;

    public CatalogClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public CatalogProductResponse getProductById(Long productId) {
        return webClient.get()
                .uri(catalogServiceUrl + "/catalog/v1/products/" + productId)
                .retrieve()
                .bodyToMono(CatalogProductResponse.class)
                .block(); // sync - we must wait for product data
    }
}