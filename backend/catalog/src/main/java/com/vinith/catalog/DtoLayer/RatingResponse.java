package com.vinith.catalog.DtoLayer;

import java.io.Serializable;

/**
 * Response DTO for Rating. Matches your Rating entity fields: id, stars, count.
 */
public class RatingResponse  implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Double stars;
    private Integer count;

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getStars() { return stars; }
    public void setStars(Double stars) { this.stars = stars; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}