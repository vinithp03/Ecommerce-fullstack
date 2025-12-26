package com.vinith.cart.RepositoryLayer;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vinith.cart.EntityLayer.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
