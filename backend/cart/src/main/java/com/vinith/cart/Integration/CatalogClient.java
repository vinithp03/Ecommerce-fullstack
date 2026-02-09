package com.vinith.cart.Integration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.vinith.cart.DtoLayer.ProductResponse;

@Component
public class CatalogClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CatalogClient(RestTemplate restTemplate, @Value("${catalog.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public ProductResponse getProductById(String id) {
        // Expects Catalog at: {baseUrl}/catalog/products/{id}
        return restTemplate.getForObject(baseUrl + "/catalog/v1/products/{id}", ProductResponse.class, id);
    }
}