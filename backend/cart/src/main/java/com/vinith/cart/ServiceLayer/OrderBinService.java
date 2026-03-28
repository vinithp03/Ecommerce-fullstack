package com.vinith.cart.ServiceLayer;

import com.vinith.cart.EntityLayer.CartItem;
import com.vinith.cart.EntityLayer.OrderBinItem;
import com.vinith.cart.RepositoryLayer.CartItemRepository;
import com.vinith.cart.RepositoryLayer.OrderBinRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderBinService {

    private final CartItemRepository cartRepo;
    private final OrderBinRepository binRepo;

    public OrderBinService(CartItemRepository cartRepo, OrderBinRepository binRepo) {
        this.cartRepo = cartRepo;
        this.binRepo = binRepo;
    }

    // Called on ORDER_CREATED
    // Move cart items → order_bin, delete from cart
    @Transactional
    public void moveToBin(Long orderId, Long userId) {
        List<CartItem> cartItems = cartRepo.findByUserId(userId);

        if (cartItems.isEmpty()) {
            System.out.printf("OrderBinService: no cart items found for userId=%d\n", userId);
            return;
        }

        // Copy each cart item → order_bin
        List<OrderBinItem> binItems = cartItems.stream()
                .map(cartItem -> {
                    OrderBinItem binItem = new OrderBinItem();
                    binItem.setOrderId(orderId);
                    binItem.setUserId(userId);
                    binItem.setProductId(cartItem.getId());
                    binItem.setImage(cartItem.getImage());
                    binItem.setItemName(cartItem.getItemName());
                    binItem.setOriginalPrice(cartItem.getOriginalPrice());
                    binItem.setCurrentPrice(cartItem.getCurrentPrice());
                    binItem.setDiscountPercentage(cartItem.getDiscountPercentage());
                    binItem.setReturnPeriod(cartItem.getReturnPeriod());
                    binItem.setDeliveryDate(cartItem.getDeliveryDate());
                    binItem.setSku(cartItem.getSku());
                    binItem.setQuantity(cartItem.getQuantity());
                    return binItem;
                })
                .toList();

        binRepo.saveAll(binItems);
        cartRepo.deleteAll(cartItems);

        System.out.printf("OrderBinService: moved %d items to bin for orderId=%d userId=%d\n",
                binItems.size(), orderId, userId);
    }

    // Called on ORDER_CANCELLED
    // Restore items from order_bin → cart, delete from bin
    @Transactional
    public void restoreFromBin(Long orderId, Long userId) {
        List<OrderBinItem> binItems = binRepo.findByOrderId(orderId);

        if (binItems.isEmpty()) {
            System.out.printf("OrderBinService: no bin items found for orderId=%d\n", orderId);
            return;
        }

        // Copy each bin item back → cart
        List<CartItem> cartItems = binItems.stream()
                .map(binItem -> {
                    CartItem cartItem = new CartItem();
                    cartItem.setId(binItem.getProductId());
                    cartItem.setUserId(userId);
                    cartItem.setImage(binItem.getImage());
                    cartItem.setItemName(binItem.getItemName());
                    cartItem.setOriginalPrice(binItem.getOriginalPrice());
                    cartItem.setCurrentPrice(binItem.getCurrentPrice());
                    cartItem.setDiscountPercentage(binItem.getDiscountPercentage());
                    cartItem.setReturnPeriod(binItem.getReturnPeriod());
                    cartItem.setDeliveryDate(binItem.getDeliveryDate());
                    cartItem.setSku(binItem.getSku());
                    cartItem.setQuantity(binItem.getQuantity());
                    return cartItem;
                })
                .toList();

        cartRepo.saveAll(cartItems);
        binRepo.deleteByOrderId(orderId);

        System.out.printf("OrderBinService: restored %d items to cart for orderId=%d userId=%d\n",
                cartItems.size(), orderId, userId);
    }

    // Called on PAYMENT_SUCCESS
    // Permanently delete from bin — order is committed
    @Transactional
    public void clearBin(Long orderId) {
        binRepo.deleteByOrderId(orderId);
        System.out.printf("OrderBinService: cleared bin for orderId=%d\n", orderId);
    }
}