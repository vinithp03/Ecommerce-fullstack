package com.vinith.catalog.DtoLayer;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO exposed to clients.
 * Mirrors entity fields and nests RatingResponse.
 */
public class ProductResponse {

    private Long id;
    private String image;
    private String company;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("original_price")
    private Integer originalPrice;

    @JsonProperty("current_price")
    private Integer currentPrice;

    @JsonProperty("discount_percentage")
    private Integer discountPercentage;

    @JsonProperty("return_period")
    private Integer returnPeriod;

    @JsonProperty("delivery_date")
    private String deliveryDate;

    private String section;
    private String sku;
    private RatingResponse rating;

    // ---- Getters and Setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Integer originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Integer currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Integer getReturnPeriod() {
        return returnPeriod;
    }

    public void setReturnPeriod(Integer returnPeriod) {
        this.returnPeriod = returnPeriod;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public RatingResponse getRating() {
        return rating;
    }

    public void setRating(RatingResponse rating) {
        this.rating = rating;
    }
}