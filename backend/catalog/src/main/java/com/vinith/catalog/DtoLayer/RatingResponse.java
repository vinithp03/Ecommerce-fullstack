package com.vinith.catalog.DtoLayer;

import java.io.Serializable;

import lombok.*;
/**
 * Response DTO for Rating. Matches your Rating entity fields: id, stars, count.
 */
@Getter
@Setter
public class RatingResponse  implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Double stars;
    private Integer count;
}