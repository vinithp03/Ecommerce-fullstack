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

    // POST: add/increment by product id (frontend hits: POST /cart/items/{id})
    @PostMapping("/items/{id}")
    public ResponseEntity<CartItemResponse> addItemByProductId(@PathVariable String id) {
        CartItemResponse saved = service.addByProductId(id); // will call Catalog GET /gap/products/{id}
        return ResponseEntity.ok(saved);
    }

    // GET: list all cart items
    @GetMapping("/items")
    public ResponseEntity<List<CartItemResponse>> getAllItems() {
        return ResponseEntity.ok(service.getAll());
    }

    // DELETE: remove by numeric id (frontend hits: DELETE /cart/items/{id})
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItemById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}