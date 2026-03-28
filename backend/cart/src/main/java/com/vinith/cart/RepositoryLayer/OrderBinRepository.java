package com.vinith.cart.RepositoryLayer;

import com.vinith.cart.EntityLayer.OrderBinItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderBinRepository extends JpaRepository<OrderBinItem, Long> {
    List<OrderBinItem> findByOrderId(Long orderId);
    List<OrderBinItem> findByUserId(Long userId);
    void deleteByOrderId(Long orderId);
}