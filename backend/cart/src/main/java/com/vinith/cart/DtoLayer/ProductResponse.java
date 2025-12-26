package com.vinith.cart.DtoLayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class ProductResponse {

    // "id": 101
    private Long id;

    // "image": "images/44.jpg"
    private String image;

    // "company": "Puma"
    private String company;

    // "section": "men"
    private String section;

    // "sku": "MEN-PUMA-RCB-K2QS"
    private String sku;

    // "item_name": "RCB Champions TShirt"
    @JsonProperty("item_name")
    private String itemName;

    // "original_price": 999
    @JsonProperty("original_price")
    private Integer originalPrice;

    // "current_price": 999
    @JsonProperty("current_price")
    private Integer currentPrice;

    // "discount_percentage": 5
    @JsonProperty("discount_percentage")
    private Integer discountPercentage;

    // "return_period": 14
    @JsonProperty("return_period")
    private Integer returnPeriod;

    // "delivery_date": "2025-10-10"
    @JsonProperty("delivery_date")
    private LocalDate deliveryDate;

    // (Optional) rating object if you need it later:
    // private Rating rating;
    // public static class Rating { private Long id; private Integer stars; private Integer count; /* getters/setters */ }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

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
}