package com.vinith.cart.Kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinith.cart.EntityLayer.CartItem;
import com.vinith.cart.RepositoryLayer.CartItemRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Optional;

@Component
public class CatalogEventConsumer {

    private final CartItemRepository cartRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    // Accept multiple date formats robustly
    private static final DateTimeFormatter DELIVERY_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendOptional(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .toFormatter();

    public CatalogEventConsumer(CartItemRepository cartRepo) {
        this.cartRepo = cartRepo;
    }

    @KafkaListener(topics = "${catalog.kafka.topic}", groupId = "cart-service")
    @Transactional
    public void onCatalogEvent(String rawJson) {
        try {
            // Parse the outer event
            JsonNode event = mapper.readTree(rawJson);
            String type = textOrNull(event, "type"); // e.g., ITEM_UPDATED / ITEM_DELETED
            String payloadStr = textOrNull(event, "payload"); // stringified JSON
            
            if (payloadStr == null) return;

            // Parse inner payload
            JsonNode payload = mapper.readTree(payloadStr);
            JsonNode current = payload.get("current");
            JsonNode changes = payload.get("changes");

            // Extract sku for logging
            String sku = (current != null && hasNonNull(current, "sku"))
                    ? current.get("sku").asText()
                    : textOrNull(event, "itemId");

            // Use numeric id for DB ops
            Long productId = (current != null && hasNonNull(current, "id")) ? current.get("id").asLong() : null;
            if (productId == null) {
                System.err.printf("CatalogEventConsumer: missing numeric id for sku=%s; skipping\n", sku);
                return;
            }

            // ---------- ITEM_DELETED ----------
            if ("ITEM_DELETED".equals(type)) {
                cartRepo.deleteById(productId); // idempotent
                System.out.printf("CatalogEventConsumer: ITEM_DELETED removed id=%d sku=%s\n", productId, sku);
                return;
            }

            // ---------- ITEM_UPDATED (UPDATE-ONLY) ----------
            if ("ITEM_UPDATED".equals(type)) {
                Optional<CartItem> maybeCartItem = cartRepo.findById(productId);
                if (maybeCartItem.isEmpty()) {
                    // Do NOT insert; just skip
                    System.out.printf("CatalogEventConsumer: ITEM_UPDATED skipped id=%d sku=%s (not in cart)\n", productId, sku);
                    return;
                }

                CartItem cartItem = maybeCartItem.get();

                // Apply only non-null changes
                if (changes != null) {
                    if (hasNonNull(changes, "item_name")) cartItem.setItemName(changes.get("item_name").asText());
                    if (hasNonNull(changes, "image")) cartItem.setImage(changes.get("image").asText());
                    if (hasNonNull(changes, "company")) cartItem.setCompany(changes.get("company").asText());

                    if (hasNonNull(changes, "original_price")) cartItem.setOriginalPrice(changes.get("original_price").asInt());
                    if (hasNonNull(changes, "current_price")) cartItem.setCurrentPrice(changes.get("current_price").asInt());
                    if (hasNonNull(changes, "discount_percentage")) cartItem.setDiscountPercentage(changes.get("discount_percentage").asInt());
                    if (hasNonNull(changes, "return_period")) cartItem.setReturnPeriod(changes.get("return_period").asInt());

                    if (hasNonNull(changes, "delivery_date")) {
                        String dateStr = changes.get("delivery_date").asText();
                        LocalDate ld = parseDeliveryDateSafe(dateStr);
                        if (ld != null) cartItem.setDeliveryDate(ld);
                        else System.err.printf("CatalogEventConsumer: invalid delivery_date '%s' for id=%d sku=%s\n", dateStr, productId, sku);
                    }
                }

                // Flush immediately so DB reflects changes
                cartRepo.saveAndFlush(cartItem);
                System.out.printf("CatalogEventConsumer: ITEM_UPDATED applied id=%d sku=%s\n", productId, sku);
                return;
            }

            // Unknown type: ignore
            System.err.printf("CatalogEventConsumer: unknown type=%s id=%d sku=%s\n", type, productId, sku);

        } catch (Exception ex) {
            System.err.printf("CatalogEventConsumer error: %s\n", ex.getMessage());
            // throw new RuntimeException(ex); // rethrow to trigger DLQ if configured
        }
    }

    // Helper methods
    private static boolean hasNonNull(JsonNode node, String field) {
        return node != null && node.has(field) && !node.get(field).isNull();
    }

    private static String textOrNull(JsonNode node, String field) {
        return (node != null && node.has(field) && !node.get(field).isNull()) ? node.get(field).asText() : null;
    }

    private LocalDate parseDeliveryDateSafe(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s, DELIVERY_DATE_FORMATTER);
        } catch (Exception ex) {
            try {
                return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception ignored) {
                return null;
            }
        }
    }
}