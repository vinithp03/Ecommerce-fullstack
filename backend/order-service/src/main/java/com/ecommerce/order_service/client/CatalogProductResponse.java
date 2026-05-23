package com.ecommerce.order_service.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogProductResponse {

    private Long id;

    private String image;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("current_price")
    private Integer currentPrice;

    // true by default (normal response), false when catalog is down
    private boolean available = true;
}