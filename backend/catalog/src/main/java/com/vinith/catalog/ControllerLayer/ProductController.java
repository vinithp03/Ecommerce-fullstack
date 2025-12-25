package com.vinith.catalog.ControllerLayer;

import com.vinith.catalog.DtoLayer.*;

import com.vinith.catalog.Events.CatalogEvent;
import com.vinith.catalog.KafkaLayer.CatalogEventProducer;
import com.vinith.catalog.ServiceLayer.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/gap")
public class ProductController {

    // --- Services injected by Spring ---
    private final ProductService productService;

    // Kafka producer to publish events
    private final CatalogEventProducer eventProducer;

    // ObjectMapper for safe JSON payloads
    private final ObjectMapper objectMapper;

    /**
     * Spring uses this constructor to inject dependencies.
     * IMPORTANT: Keep a single constructor and include all required beans here.
     */
    public ProductController(ProductService productService,
                             CatalogEventProducer eventProducer,
                             ObjectMapper objectMapper) {
        this.productService = productService;
        this.eventProducer = eventProducer;
        this.objectMapper = objectMapper;
    }

    // --- READ ALL ---
    @GetMapping(path = "/products", produces = "application/json")
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProductsAsDto();
    }

    // --- READ ONE by ID ---
    @GetMapping(path = "/products/{id}", produces = "application/json")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getByIdAsDto(id);
        return ResponseEntity.ok(product);
    }

    // --- READ ONE by SKU ---
    @GetMapping(path = "/products/by-sku/{sku}", produces = "application/json")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        ProductResponse product = productService.getBySkuAsDto(sku);
        return ResponseEntity.ok(product);
    }

    // --- CREATE SINGLE (emits ITEM_CREATED) ---
    @PostMapping(path = "/product", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        try {
            ProductResponse resp = productService.create(request);
            URI location = URI.create(String.format("/gap/products/%d", resp.getId()));

            // Publish ITEM_CREATED (SKU as Kafka key)
            String payload = toJson(Map.of(
                    "id", resp.getId(),
                    "sku", resp.getSku()
            ));

            CatalogEvent event = new CatalogEvent(
                    UUID.randomUUID().toString(),
                    "ITEM_CREATED",
                    resp.getSku(), // Kafka key: SKU
                    payload,
                    System.currentTimeMillis()
            );

            eventProducer.publishDefault(event); // async

            return ResponseEntity.created(location).body(resp); // 201 Created
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error("BAD_REQUEST", ex.getMessage())); // 400
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            return ResponseEntity.status(409).body(error("CONFLICT", ex.getMessage())); // 409 Conflict
        }
    }

    // --- BULK CREATE ---
    @PostMapping(path = "/products/bulk", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createProducts(@Valid @RequestBody List<ProductCreateRequest> requests) {
        try {
            List<ProductResponse> resp = productService.createAll(requests);
            return ResponseEntity.status(201).body(resp); // 201 Created
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error("BAD_REQUEST", ex.getMessage()));
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            return ResponseEntity.status(409).body(error("CONFLICT", ex.getMessage()));
        }
    }

    // --- PARTIAL UPDATE BY SKU (emits ITEM_UPDATED) ---
    @PatchMapping(path = "/products/{sku}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> patchBySku(@PathVariable String sku, @Valid @RequestBody ProductPatchRequest req) {
        try {
            ProductResponse resp = productService.patchBySku(sku, req);

            // Publish ITEM_UPDATED
            String payload = toJson(Map.of(
                    "sku", sku,
                    "changes", req,
                    "current", Map.of("id", resp.getId(), "sku", resp.getSku())
            ));

            CatalogEvent event = new CatalogEvent(
                    UUID.randomUUID().toString(),
                    "ITEM_UPDATED",
                    sku, // Kafka key: SKU
                    payload,
                    System.currentTimeMillis()
            );

            eventProducer.publishDefault(event); // async

            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("NOT_FOUND", ex.getMessage()));
        }
    }

    // --- DELETE by ID (emits ITEM_DELETED) ---
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        productService.deleteById(id);

        // Publish ITEM_DELETED (by ID). If SKU isn't known, use ID as key.
        String payload = toJson(Map.of("id", id));
        CatalogEvent event = new CatalogEvent(
                UUID.randomUUID().toString(),
                "ITEM_DELETED",
                String.valueOf(id), // Kafka key: ID (SKU not available)
                payload,
                System.currentTimeMillis()
        );

        eventProducer.publishDefault(event); // async

        return ResponseEntity.noContent().build(); // 204
    }

    // --- DELETE by SKU (emits ITEM_DELETED) ---
    @DeleteMapping("/products/by-sku/{sku}")
    public ResponseEntity<?> deleteBySku(@PathVariable String sku) {
        long count = productService.deleteBySku(sku);
        if (count == 0) {
            return ResponseEntity.status(404).body(error("NOT_FOUND", "No product found for SKU: " + sku));
        }

        // Publish ITEM_DELETED
        String payload = toJson(Map.of(
                "sku", sku,
                "deletedCount", count
        ));

        CatalogEvent event = new CatalogEvent(
                UUID.randomUUID().toString(),
                "ITEM_DELETED",
                sku, // Kafka key: SKU
                payload,
                System.currentTimeMillis()
        );

        eventProducer.publishDefault(event); // async

        return ResponseEntity.noContent().build(); // 204
    }

    // Manual publish endpoint (Kafka test without DB changes)
    @PostMapping(path = "/products/{sku}/event/{type}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> publishCatalogEvent(@PathVariable String sku,
                                                 @PathVariable String type,
                                                 @RequestBody(required = false) String payload) {
        CatalogEvent event = new CatalogEvent(
                UUID.randomUUID().toString(),
                type,
                sku,
                (payload == null || payload.isBlank()) ? "{}" : payload,
                System.currentTimeMillis()
        );

        try {
            eventProducer.publishDefault(event); // async
            return ResponseEntity.accepted()
                    .body(new EventPublishResponse("PUBLISHED", "Event " + type + " queued for SKU " + sku));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(error("INTERNAL_ERROR", ex.getMessage()));
        }
    }

    // --- Helpers ---
    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "{\"error\":\"payload-serialization-failed\"}";
        }
    }

    private static ErrorResponse error(String code, String message) {
        return new ErrorResponse(code, message);
    }

    // --- Small DTOs for responses ---
    static class ErrorResponse {
        public final String code;
        public final String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    static class EventPublishResponse {
        public final String status;
        public final String message;

        public EventPublishResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}