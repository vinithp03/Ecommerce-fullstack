package com.vinith.cart.DtoLayer;

import java.time.LocalDate;

public class CartItemResponse {
    private Long id; // The catalog product id (used as cart PK)
    private String image;
    private String itemName;
    private Integer originalPrice;
    private Integer currentPrice;
    private Integer discountPercentage;
    private Integer returnPeriod;
    private LocalDate deliveryDate;
    private String sku;
    private int quantity;

    public CartItemResponse() {}

    public CartItemResponse(Long id, String image, String itemName, Integer originalPrice, Integer currentPrice, 
                            Integer discountPercentage, Integer returnPeriod, LocalDate deliveryDate, String sku, int quantity) {
        super();
        this.id = id;
        this.image = image;
        this.itemName = itemName;
        this.originalPrice = originalPrice;
        this.currentPrice = currentPrice;
        this.discountPercentage = discountPercentage;
        this.returnPeriod = returnPeriod;
        this.deliveryDate = deliveryDate;
        this.sku = sku;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

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

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
