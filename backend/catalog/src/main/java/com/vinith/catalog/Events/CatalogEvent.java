package com.vinith.catalog.Events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatalogEvent {
    private String eventId;
    private String type; // ITEM_CREATED, ITEM_UPDATED, ITEM_DELETED
    private String itemId;
    private String payload; // simple JSON string for now
    private long timestamp;
}