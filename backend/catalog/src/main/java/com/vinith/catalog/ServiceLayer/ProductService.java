package com.vinith.catalog.ServiceLayer;

import com.vinith.catalog.DtoLayer.BulkCreateResponse;
import com.vinith.catalog.DtoLayer.ProductCreateRequest;
import com.vinith.catalog.DtoLayer.ProductPatchRequest;
import com.vinith.catalog.DtoLayer.ProductResponse;
import com.vinith.catalog.EntityLayer.Product;
import com.vinith.catalog.MapperLayer.ProductMapper;
import com.vinith.catalog.RepositoryLayer.ProductRepository;
import jakarta.validation.Valid;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    private String norm(String s) {
        return s == null ? null : s.trim();
    }

    // ---------- Read (Entity) for legacy/internal use ----------
    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    @Cacheable(value = "productById", key = "#id")
    public Product getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }
    
    @Cacheable(
    	    value = "productBySku",
    	    key = "T(java.lang.String).valueOf(#sku).trim()"
    	)    public Product getBySku(String sku) {
        return repo.findBySku(norm(sku)).orElseThrow(() -> new IllegalArgumentException("Product not found for SKU: " + sku));
    }

    // ---------- Read (DTO) for controllers ----------
    @Cacheable(value = "products", key = "'all'")
    public List<ProductResponse> getAllProductsAsDto() {
        return repo.findAll().stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getByIdAsDto(Long id) {
        return ProductMapper.toResponse(getById(id));
    }

    public ProductResponse getBySkuAsDto(String sku) {
        return ProductMapper.toResponse(getBySku(sku));
    }

    // ---------- Create (Entity body) for legacy/internal endpoints ----------
    @CacheEvict(value = {"productById", "productBySku"}, allEntries = true)
    @Transactional
    public Product create(Product incoming) {
        // Normalize and force INSERT
        incoming.setCompany(norm(incoming.getCompany()));
        incoming.setItem_name(norm(incoming.getItem_name()));
        incoming.setSection(norm(incoming.getSection()));
        incoming.setSku(norm(incoming.getSku()));
        incoming.setImage(norm(incoming.getImage()));
        incoming.setDelivery_date(norm(incoming.getDelivery_date()));
        incoming.setId(null);
        if (incoming.getRating() != null) incoming.getRating().setId(null);

        // SKU checks
        if (incoming.getSku() == null || incoming.getSku().isBlank()) {
            throw new IllegalArgumentException("SKU must be provided for creation");
        }
        if (repo.existsBySku(incoming.getSku())) {
            throw new DataIntegrityViolationException("SKU already exists: " + incoming.getSku());
        }

        try {
            return repo.save(incoming);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("SKU already exists: " + incoming.getSku(), e);
        }
    }
    
    
    @Transactional
    public List<Product> createAllEntities(List<Product> products) {
        if (products == null || products.isEmpty()) return List.of();
        return products.stream()
                .map(this::create)
                .collect(Collectors.toList());
    }

    // ---------- Create (DTO) for controllers ----------
    @CacheEvict(value = {"productById", "productBySku"}, allEntries = true)
    @Transactional
    public ProductResponse create(@Valid ProductCreateRequest req) {
        Product incoming = ProductMapper.fromCreate(req);
        // Force INSERT rule for DTO path as well
        incoming.setId(null);

        if (incoming.getSku() == null || incoming.getSku().isBlank()) {
            throw new IllegalArgumentException("SKU must be provided for creation");
        }
        if (repo.existsBySku(incoming.getSku())) {
            throw new DataIntegrityViolationException("SKU already exists: " + incoming.getSku());
        }

        try {
            Product saved = repo.save(incoming);
            return ProductMapper.toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("SKU already exists: " + incoming.getSku(), e);
        }
    }
    
    /**
     * Strict bulk create (DTO): reuses single create logic - rejects duplicates at pre-
     * Kept for reference; your controller now uses bulkCreateSkipExisting(...) instead.
     */
    @Transactional
    public List<ProductResponse> createAll(@Valid List<ProductCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) return List.of();
        return requests.stream()
                .map(this::create)
                .collect(Collectors.toList());
    }

    // ---------- NEW: Bulk create (DTO) - skip existing SKUs, insert remaining ----------
    @CacheEvict(value = {"productById", "productBySku"}, allEntries = true)
    @Transactional
    public BulkCreateResponse bulkCreateSkipExisting(@Valid List<ProductCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return new BulkCreateResponse(List.of(), List.of());
        }

        // Normalize SKUs and dedupe incoming payload (preserve order)
        Map<String, ProductCreateRequest> bySku = new LinkedHashMap<>();
        for (ProductCreateRequest r : requests) {
            String sku = norm(r.getSku());
            if (sku == null || sku.isBlank()) {
                continue; // or throw exception
            }
            bySku.putIfAbsent(sku, r);
        }

        List<String> incomingSkus = new ArrayList<>(bySku.keySet());

        // Find which SKUs already exist (single DB call)
        List<Product> existing = repo.findBySkuIn(incomingSkus);
        Set<String> existingSkus = existing.stream()
                .map(Product::getSku)
                .collect(Collectors.toSet());

        // Partition SKUs
        List<String> toInsertSkus = incomingSkus.stream()
                .filter(sku -> !existingSkus.contains(sku))
                .collect(Collectors.toList());

        List<String> skippedSkus = incomingSkus.stream()
                .filter(existingSkus::contains)
                .collect(Collectors.toList());

        // Build entities to insert (force INSERT semantics)
        List<Product> toInsert = toInsertSkus.stream()
                .map(sku -> ProductMapper.fromCreate(bySku.get(sku)))
                .peek(p -> p.setId(null))
                .collect(Collectors.toList());

        // Persist and map to DTOs
        List<Product> inserted = repo.saveAll(toInsert);
        List<ProductResponse> insertedDtos = inserted.stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());

        return new BulkCreateResponse(insertedDtos, skippedSkus);
    }

    // ---------- Update SKU by ID (optional) ----------
    @CacheEvict(value = {"productById", "productBySku"}, allEntries = true)
    @Transactional
    public Product updateSku(Long productId, String newSku) {
        Product existing = getById(productId);
        newSku = norm(newSku);

        if (newSku == null || newSku.isBlank()) throw new IllegalArgumentException("New SKU must not be blank");
        if (repo.existsBySku(newSku)) throw new DataIntegrityViolationException("SKU already exists: " + newSku);

        existing.setSku(newSku);
        try {
            return repo.save(existing);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("SKU already exists: " + newSku, e);
        }
    }

    // ---------- PATCH by SKU (DTO) ----------
    @CacheEvict(value = {"productById", "productBySku"}, allEntries = true)
    @Transactional
    public ProductResponse patchBySku(String sku, @Valid ProductPatchRequest req) {
        Product p = getBySku(sku); // normalized inside getBySku
        ProductMapper.applyPatch(p, req);
        Product saved = repo.save(p);
        return ProductMapper.toResponse(saved);
    }

    // ---------- Delete ----------
    @CacheEvict(value = {"productById", "productBySku"}, allEntries = true)
    @Transactional
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
    
    @CacheEvict(value = {"productById", "productBySku"}, allEntries = true)
    @Transactional
    public long deleteBySku(String sku) {
        return repo.deleteBySku(norm(sku));
    }

    // ---------- Optional ----------
    public boolean existsBySku(String sku) {
        return repo.existsBySku(norm(sku));
    }
}