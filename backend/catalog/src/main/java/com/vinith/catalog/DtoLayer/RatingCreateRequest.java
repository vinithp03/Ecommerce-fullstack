package com.vinith.catalog.DtoLayer;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;

public class RatingCreateRequest {
    @DecimalMin("0.0") @DecimalMax("5.0")
    private Double stars;

    @PositiveOrZero
    private Integer count;

    public Double getStars() { return stars; }
    public void setStars(Double stars) { this.stars = stars; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}