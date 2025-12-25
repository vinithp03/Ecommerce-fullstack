package com.vinith.catalog.RepositoryLayer;

import com.vinith.catalog.EntityLayer.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /** SKU is the unique business key */
    boolean existsBySku(String sku);

    Optional<Product> findBySku(String sku);

    long deleteBySku(String sku);

    List<Product> findBySkuIn(Collection<String> skus);

    // Keep these ONLY for searching/filtering; do NOT use for create/update decisions.
    @Query("""
            SELECT p FROM Product p
            WHERE p.company = :company
            AND p.item_name  = :itemName
            AND p.section = :section
            """)
    Optional<Product> findByCompanyItemNameSection(@Param("company") String company, 
                                                   @Param("itemName") String itemName, 
                                                   @Param("section") String section);

    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END
            FROM Product p
            WHERE p.company = :company
            AND p.item_name  = :itemName
            AND p.section = :section
            """)
    boolean existsByCompanyItemNameSection(@Param("company") String company, 
                                           @Param("itemName") String itemName, 
                                           @Param("section") String section);
}