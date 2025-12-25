package com.vinith.catalog.Events;

import java.util.Objects;

public class CatalogEvent {
    private String eventId;
    private String type; // ITEM_CREATED, ITEM_UPDATED, ITEM_DELETED
    private String itemId;
    private String payload; // simple JSON string for now
    private long timestamp;

    public CatalogEvent() {
    }

    public CatalogEvent(String eventId, String type, String itemId, String payload, long timestamp) {
        this.eventId = eventId;
        this.type = type;
        this.itemId = itemId;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    // Getters
    public String getEventId() { return eventId; }
    public String getType() { return type; }
    public String getItemId() { return itemId; }
    public String getPayload() { return payload; }
    public long getTimestamp() { return timestamp; }

    // Setters
    public void setEventId(String eventId) { this.eventId = eventId; }
    public void setType(String type) { this.type = type; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setPayload(String payload) { this.payload = payload; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "CatalogEvent{" +
                "eventId='" + eventId + '\'' +
                ", type='" + type + '\'' +
                ", itemId='" + itemId + '\'' +
                ", payload='" + payload + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CatalogEvent)) return false;
        CatalogEvent that = (CatalogEvent) o;
        return timestamp == that.timestamp &&
                Objects.equals(eventId, that.eventId) &&
                Objects.equals(type, that.type) &&
                Objects.equals(itemId, that.itemId) &&
                Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, type, itemId, payload, timestamp);
    }
}