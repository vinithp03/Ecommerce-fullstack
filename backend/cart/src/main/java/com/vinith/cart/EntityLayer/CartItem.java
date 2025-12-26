package com.vinith.cart.EntityLayer;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    // Use Catalog product id as the cart primary key
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "image")
    private String image;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "company") // optional, available in your catalog JSON
    private String company;

    @Column(name = "original_price")
    private Integer originalPrice;

    @Column(name = "current_price")
    private Integer currentPrice;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    @Column(name = "return_period")
    private Integer returnPeriod;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "sku")
    private String sku;

    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    public CartItem() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public Integer getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(Integer originalPrice) { this.originalPrice = originalPrice; }

    public Integer getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(Integer currentPrice) { this.currentPrice = currentPrice; }

    public Integer getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Integer discountPercentage) { this.discountPercentage = discountPercentage; }

    public Integer getReturnPeriod() { return returnPeriod; }
    public void setReturnPeriod(Integer returnPeriod) { this.returnPeriod = returnPeriod; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}