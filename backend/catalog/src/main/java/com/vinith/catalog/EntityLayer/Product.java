package com.vinith.catalog.EntityLayer;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "products",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_sku", columnNames = {"sku"})
    },
    indexes = {
        @Index(name = "idx_company", columnList = "company"),
        @Index(name = "idx_item_name", columnList = "item_name"),
        @Index(name = "idx_section", columnList = "section")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "rating")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image")
    private String image;

    @Column(name = "company", nullable = false)
    private String company;

    @Column(name = "item_name", nullable = false)
    private String item_name;

    @Column(name = "original_price")
    private int original_price;

    @Column(name = "current_price")
    private int current_price;

    @Column(name = "discount_percentage")
    private int discount_percentage;

    @Column(name = "return_period")
    private int return_period;

    @Column(name = "delivery_date")
    private String delivery_date;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rating_id", referencedColumnName = "id")
    private Rating rating;

    @Column(name = "section")
    private String section;

    @Column(name = "sku", nullable = false, unique = true, length = 40)
    private String sku;
}
