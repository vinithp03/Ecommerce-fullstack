package com.vinith.cart.ServiceLayer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vinith.cart.DtoLayer.CartItemResponse;
import com.vinith.cart.DtoLayer.ProductResponse;
import com.vinith.cart.EntityLayer.CartItem;
import com.vinith.cart.Integration.CatalogClient;
import com.vinith.cart.RepositoryLayer.CartItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemService {

    private final CartItemRepository repo;
    private final CatalogClient catalogClient;

    public CartItemService(CartItemRepository repo, CatalogClient catalogClient) {
        this.repo = repo;
        this.catalogClient = catalogClient;
    }

    // POST /cart/v1/users/{userId}/items/{productId}
    @Transactional
    public CartItemResponse addByProductId(Long userId, String idStr) {
        Long id = parseId(idStr);

        ProductResponse product = catalogClient.getProductById(idStr);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + idStr);
        }

        Optional<CartItem> existingOpt = repo.findByUserIdAndId(userId, id);
        CartItem entity = existingOpt.map(e -> {
            e.setQuantity(e.getQuantity() + 1);
            return e;
        }).orElseGet(() -> {
            CartItem e = new CartItem();
            e.setId(id);
            e.setUserId(userId);
            e.setImage(product.getImage());
            e.setItemName(product.getItemName());
            e.setOriginalPrice(product.getOriginalPrice());
            e.setCurrentPrice(product.getCurrentPrice());
            e.setDiscountPercentage(product.getDiscountPercentage());
            e.setReturnPeriod(product.getReturnPeriod());
            e.setDeliveryDate(product.getDeliveryDate());
            e.setSku(product.getSku());
            e.setQuantity(1);
            return e;
        });

        CartItem saved = repo.save(entity);
        return toResponse(saved);
    }

    // GET /cart/v1/users/{userId}/items
    @Transactional(readOnly = true)
    public List<CartItemResponse> getAll(Long userId) {
        return repo.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    // DELETE /cart/v1/users/{userId}/items/{id}
    @Transactional
    public void deleteById(Long userId, Long id) {
        repo.deleteByUserIdAndId(userId, id);
    }

    private Long parseId(String idStr) {
        try {
            return Long.valueOf(idStr);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid id: " + idStr);
        }
    }

    private CartItemResponse toResponse(CartItem item) {
        CartItemResponse r = new CartItemResponse();
        r.setId(item.getId());
        r.setImage(item.getImage());
        r.setItemName(item.getItemName());
        r.setOriginalPrice(item.getOriginalPrice());
        r.setCurrentPrice(item.getCurrentPrice());
        r.setDiscountPercentage(item.getDiscountPercentage());
        r.setReturnPeriod(item.getReturnPeriod());
        r.setDeliveryDate(item.getDeliveryDate());
        r.setSku(item.getSku());
        r.setQuantity(item.getQuantity());
        return r;
    }
}