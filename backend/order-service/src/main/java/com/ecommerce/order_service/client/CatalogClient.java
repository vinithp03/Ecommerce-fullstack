package com.ecommerce.order_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CatalogClient {

    private static final Logger log =
            LoggerFactory.getLogger(CatalogClient.class);

    private final WebClient webClient;

    @Value("${catalog.service.url}")
    private String catalogServiceUrl;

    public CatalogClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Retry(name = "catalogService")
    @CircuitBreaker(
            name = "catalogService",
            fallbackMethod = "getProductFallback"
    )
    public CatalogProductResponse getProductById(Long productId) {

        log.info("Calling catalog-service for productId: {}", productId);

        return webClient.get()
                .uri(catalogServiceUrl + "/catalog/v1/products/" + productId)
                .retrieve()
                .bodyToMono(CatalogProductResponse.class)
                .block();
    }

    public CatalogProductResponse getProductFallback(
            Long productId,
            Exception ex
    ) {

        log.warn(
                "[CATALOG] Fallback triggered for productId: {} Cause: {}",
                productId,
                ex.getMessage()
        );

        CatalogProductResponse fallback =
                new CatalogProductResponse();

        fallback.setId(productId);
        fallback.setItemName("Product Unavailable");
        fallback.setCurrentPrice(0);
        fallback.setAvailable(false);

        return fallback;
    }
}