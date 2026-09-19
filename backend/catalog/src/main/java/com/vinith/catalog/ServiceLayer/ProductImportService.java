package com.vinith.catalog.ServiceLayer;

import com.vinith.catalog.EntityLayer.Product;
import com.vinith.catalog.EntityLayer.Rating;
import com.vinith.catalog.RepositoryLayer.ProductRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductImportService {

    private final ProductRepository productRepository;

    public ProductImportService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public int importProducts(MultipartFile file) throws Exception {

        List<Product> products = new ArrayList<>();
        int importedCount = 0;

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                file.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                );

                CSVParser csvParser = CSVFormat.DEFAULT
                        .builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(reader)
        ) {

            for (CSVRecord record : csvParser) {

                Product product = new Product();

                product.setImage(record.get("image"));
                product.setCompany(record.get("company"));
                product.setItem_name(record.get("item_name"));

                product.setOriginal_price(
                        Integer.parseInt(record.get("original_price"))
                );

                product.setCurrent_price(
                        Integer.parseInt(record.get("current_price"))
                );

                product.setDiscount_percentage(
                        Integer.parseInt(record.get("discount_percentage"))
                );

                product.setReturn_period(
                        Integer.parseInt(record.get("return_period"))
                );

                product.setDelivery_date(
                        record.get("delivery_date")
                );

                product.setSection(
                        record.get("section")
                );

                product.setSku(
                        record.get("sku")
                );

                Rating rating = new Rating();

                rating.setStars(
                        Double.parseDouble(record.get("rating_value"))
                );

                rating.setCount(
                        Integer.parseInt(record.get("rating_count"))
                );

                product.setRating(rating);

                products.add(product);
                importedCount++;

                if (products.size() == 500) {

                    productRepository.saveAll(products);
                    productRepository.flush();

                    products.clear();
                }
            }

            if (!products.isEmpty()) {

                productRepository.saveAll(products);
                productRepository.flush();
            }
        }

        return importedCount;
    }
}