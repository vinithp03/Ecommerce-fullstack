package com.vinith.cart.RepositoryLayer;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vinith.cart.EntityLayer.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserIdAndId(Long userId, Long productId);
    void deleteByUserIdAndId(Long userId, Long productId);
}
