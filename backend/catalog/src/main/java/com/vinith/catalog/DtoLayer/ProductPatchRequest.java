package com.vinith.catalog.DtoLayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductPatchRequest {

    private String image;
    private String company;

    @JsonProperty("item_name")
    private String itemName;

    @PositiveOrZero(message = "original_price must be >= 0")
    @JsonProperty("original_price")
    private Integer originalPrice;

    @PositiveOrZero(message = "current_price must be >= 0")
    @JsonProperty("current_price")
    private Integer currentPrice;

    @Min(value = 0)
    @Max(value = 100)
    @JsonProperty("discount_percentage")
    private Integer discountPercentage;

    @PositiveOrZero(message = "return_period must be >= 0")
    @JsonProperty("return_period")
    private Integer returnPeriod;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "delivery_date must be YYYY-MM-DD")
    @JsonProperty("delivery_date")
    private String deliveryDate;

    private String section;
}