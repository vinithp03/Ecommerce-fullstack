package com.vinith.catalog.DtoLayer;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Response DTO exposed to clients.
 * Mirrors entity fields and nests RatingResponse.
 */
@Getter
@Setter
public class ProductResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String image;
    private String company;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("original_price")
    private Integer originalPrice;

    @JsonProperty("current_price")
    private Integer currentPrice;

    @JsonProperty("discount_percentage")
    private Integer discountPercentage;

    @JsonProperty("return_period")
    private Integer returnPeriod;

    @JsonProperty("delivery_date")
    private String deliveryDate;

    private String section;
    private String sku;
    private RatingResponse rating;

}