package com.vinith.catalog.EntityLayer;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DecimalMin("0.0") @DecimalMax("5.0")
    @Column(name = "stars")
    private Double stars;

    @PositiveOrZero
    @Column(name = "count")
    private Integer count;

    // -- Constructors --
    public Rating() {}

    public Rating(Long id, Double stars, Integer count) {
        this.id = id;
        this.stars = stars;
        this.count = count;
    }

    // -- Getters and Setters --
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getStars() { return stars; }
    public void setStars(Double stars) { this.stars = stars; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}