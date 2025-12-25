package com.vinith.catalog.MapperLayer;

import com.vinith.catalog.DtoLayer.ProductCreateRequest;
import com.vinith.catalog.DtoLayer.ProductPatchRequest;
import com.vinith.catalog.DtoLayer.ProductResponse;
import com.vinith.catalog.DtoLayer.RatingCreateRequest;
import com.vinith.catalog.DtoLayer.RatingResponse;
import com.vinith.catalog.EntityLayer.Product;
import com.vinith.catalog.EntityLayer.Rating;

public class ProductMapper {

    // ---------- Entity -> Response DTO ----------
    public static ProductResponse toResponse(Product p) {
        if (p == null) return null;

        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setImage(p.getImage());
        r.setCompany(p.getCompany());
        r.setItemName(p.getItem_name());
        r.setOriginalPrice(p.getOriginal_price());
        r.setCurrentPrice(p.getCurrent_price());
        r.setDiscountPercentage(p.getDiscount_percentage());
        r.setReturnPeriod(p.getReturn_period());
        r.setDeliveryDate(p.getDelivery_date());
        r.setSection(p.getSection());
        r.setSku(p.getSku());

        Rating rating = p.getRating();
        if (rating != null) {
            RatingResponse rr = new RatingResponse();
            rr.setId(rating.getId());
            rr.setStars(rating.getStars());
            rr.setCount(rating.getCount());
            r.setRating(rr);
        }

        return r;
    }

    // ---------- CreateRequest DTO -> Entity (includes Rating if present) ----------
    public static Product fromCreate(ProductCreateRequest req) {
        Product p = new Product();
        p.setImage(norm(req.getImage()));
        p.setCompany(norm(req.getCompany()));
        p.setItem_name(norm(req.getItemName()));
        p.setOriginal_price(orZero(req.getOriginalPrice()));
        p.setCurrent_price(orZero(req.getCurrentPrice()));
        p.setDiscount_percentage(orZero(req.getDiscountPercentage()));
        p.setReturn_period(orZero(req.getReturnPeriod()));
        p.setDelivery_date(norm(req.getDeliveryDate()));
        p.setSection(norm(req.getSection()));
        p.setSku(norm(req.getSku()));

        // Map rating if provided in request
        RatingCreateRequest rReq = req.getRating();
        if (rReq != null) {
            Rating r = new Rating();
            r.setId(null); // ensure a fresh row for rating
            r.setStars(rReq.getStars());
            r.setCount(rReq.getCount());
            p.setRating(r);
        }

        return p;
    }

    // ---------- Apply PATCH (partial update) ----------
    public static void applyPatch(Product p, ProductPatchRequest req) {
        if (req.getImage() != null) p.setImage(norm(req.getImage()));
        if (req.getCompany() != null) p.setCompany(norm(req.getCompany()));
        if (req.getItemName() != null) p.setItem_name(norm(req.getItemName()));
        if (req.getOriginalPrice() != null) p.setOriginal_price(req.getOriginalPrice());
        if (req.getCurrentPrice() != null) p.setCurrent_price(req.getCurrentPrice());
        if (req.getDiscountPercentage() != null) p.setDiscount_percentage(req.getDiscountPercentage());
        if (req.getReturnPeriod() != null) p.setReturn_period(req.getReturnPeriod());
        if (req.getDeliveryDate() != null) p.setDelivery_date(norm(req.getDeliveryDate()));
        if (req.getSection() != null) p.setSection(norm(req.getSection()));
        // NOTE: rating is not patched here
        // NOTE: SKU is intentionally NOT changed here
    }

    // ---------- Small helpers ----------
    private static int orZero(Integer v) { return v == null ? 0 : v; }
    private static String norm(String s) { return s == null ? null : s.trim(); }
}