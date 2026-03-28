package com.vinith.cart.ControllerLayer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vinith.cart.DtoLayer.CartItemResponse;
import com.vinith.cart.ServiceLayer.CartItemService;
import java.util.List;

@CrossOrigin("http://localhost:5173")
@RestController
@RequestMapping("/cart/v1")
public class CartItemController {

    private final CartItemService service;

    public CartItemController(CartItemService service) {
        this.service = service;
    }

    // POST /cart/v1/users/{userId}/items/{productId}
    @PostMapping("/users/{userId}/items/{productId}")
    public ResponseEntity<CartItemResponse> addItemByProductId(
            @PathVariable Long userId,
            @PathVariable String productId) {
        CartItemResponse saved = service.addByProductId(userId, productId);
        return ResponseEntity.ok(saved);
    }

    // GET /cart/v1/users/{userId}/items
    @GetMapping("/users/{userId}/items")
    public ResponseEntity<List<CartItemResponse>> getAllItems(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getAll(userId));
    }

    // DELETE /cart/v1/users/{userId}/items/{id}
    @DeleteMapping("/users/{userId}/items/{id}")
    public ResponseEntity<Void> deleteItemById(
            @PathVariable Long userId,
            @PathVariable Long id) {
        service.deleteById(userId, id);
        return ResponseEntity.noContent().build();
    }
}