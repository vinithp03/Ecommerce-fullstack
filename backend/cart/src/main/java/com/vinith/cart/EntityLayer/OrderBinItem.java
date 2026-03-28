package com.vinith.cart.EntityLayer;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_bin")
public class OrderBinItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;    // which order this belongs to
    private Long userId;     // which user
    private Long productId;  // original product id from catalog

    private String image;
    private String itemName;
    private Integer originalPrice;
    private Integer currentPrice;
    private Integer discountPercentage;
    private Integer returnPeriod;
    private LocalDate deliveryDate;
    private String sku;
    private int quantity;

    private LocalDateTime movedAt;  // when it was moved to bin

    @PrePersist
    protected void onCreate() {
        movedAt = LocalDateTime.now();
    }

    public OrderBinItem() {}

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

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

    public LocalDateTime getMovedAt() { return movedAt; }
    public void setMovedAt(LocalDateTime movedAt) { this.movedAt = movedAt; }
}