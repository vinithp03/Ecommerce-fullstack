package com.vinith.catalog.ControllerLayer;

import com.vinith.catalog.ServiceLayer.ProductImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/catalog/v1/products")
public class ProductImportController {

    private final ProductImportService productImportService;

    public ProductImportController(
            ProductImportService productImportService
    ) {
        this.productImportService = productImportService;
    }

    @PostMapping("/import")
    public ResponseEntity<?> importProducts(
            @RequestParam("file") MultipartFile file
    ) {

        try {

            int count = productImportService.importProducts(file);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Products imported successfully",
                            "count", count
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "message", "Product import failed",
                            "error", e.getMessage()
                    )
            );
        }
    }
}