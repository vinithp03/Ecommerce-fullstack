package com.vinith.catalog.DtoLayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class ProductPatchRequest {

    private String image;
    private String company;

    @JsonProperty("item_name")
    private String itemName;

    @PositiveOrZero(message = "original_price must be >= 0")
    @JsonProperty("original_price")
    private Integer originalPrice;

    @PositiveOrZero(message = "current_price must be >= 0")
    @JsonProperty("current_price")
    private Integer currentPrice;

    @Min(value = 0)
    @Max(value = 100)
    @JsonProperty("discount_percentage")
    private Integer discountPercentage;

    @PositiveOrZero(message = "return_period must be >= 0")
    @JsonProperty("return_period")
    private Integer returnPeriod;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "delivery_date must be YYYY-MM-DD")
    @JsonProperty("delivery_date")
    private String deliveryDate;

    private String section;

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

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

    public String getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
}