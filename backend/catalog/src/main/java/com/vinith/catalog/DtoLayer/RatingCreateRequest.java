package com.vinith.catalog.DtoLayer;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingCreateRequest {
    @DecimalMin("0.0") @DecimalMax("5.0")
    private Double stars;

    @PositiveOrZero
    private Integer count;
}