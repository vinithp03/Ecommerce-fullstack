package com.vinith.catalog.EntityLayer;

import java.io.Serializable;

import jakarta.persistence.*;

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

    // -- No-args constructor --
    public Product() {}

    // -- All-args constructor --
    public Product(Long id, String image, String company, String item_name, int original_price, int current_price, int discount_percentage, int return_period, String delivery_date, Rating rating, String section, String sku) {
        this.id = id;
        this.image = image;
        this.company = company;
        this.item_name = item_name;
        this.original_price = original_price;
        this.current_price = current_price;
        this.discount_percentage = discount_percentage;
        this.return_period = return_period;
        this.delivery_date = delivery_date;
        this.rating = rating;
        this.section = section;
        this.sku = sku;
    }

    // -- Getters and Setters --
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getItem_name() { return item_name; }
    public void setItem_name(String item_name) { this.item_name = item_name; }

    public int getOriginal_price() { return original_price; }
    public void setOriginal_price(int original_price) { this.original_price = original_price; }

    public int getCurrent_price() { return current_price; }
    public void setCurrent_price(int current_price) { this.current_price = current_price; }

    public int getDiscount_percentage() { return discount_percentage; }
    public void setDiscount_percentage(int discount_percentage) { this.discount_percentage = discount_percentage; }

    public int getReturn_period() { return return_period; }
    public void setReturn_period(int return_period) { this.return_period = return_period; }

    public String getDelivery_date() { return delivery_date; }
    public void setDelivery_date(String delivery_date) { this.delivery_date = delivery_date; }

    public Rating getRating() { return rating; }
    public void setRating(Rating rating) { this.rating = rating; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", image='" + image + '\'' +
                ", company='" + company + '\'' +
                ", item_name='" + item_name + '\'' +
                ", original_price=" + original_price +
                ", current_price=" + current_price +
                ", discount_percentage=" + discount_percentage +
                ", return_period=" + return_period +
                ", delivery_date='" + delivery_date + '\'' +
                ", section='" + section + '\'' +
                ", sku='" + sku + '\'' +
                '}';
    }
}