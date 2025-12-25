package com.vinith.catalog.KafkaLayer;

import com.vinith.catalog.Events.CatalogEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Produces CatalogEvent messages to Kafka. - Async publishing (non-blocking).
 * Supports default topic, explicit topic/key, and optional headers. - Returns
 * CompletableFuture for composition/testing.
 */
@Service
public class CatalogEventProducer {

    private static final Logger Log = LoggerFactory.getLogger(CatalogEventProducer.class);

    private final KafkaTemplate<String, CatalogEvent> kafkaTemplate;
    private final String defaultTopic;

    public CatalogEventProducer(KafkaTemplate<String, CatalogEvent> kafkaTemplate,
                                @Value("${spring.kafka.template.default-topic:catalog.events}") String defaultTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.defaultTopic = defaultTopic;
    }

    /**
     * Publish to the default topic using event.getItemId() as the Kafka key.
     */
    public CompletableFuture<SendResult<String, CatalogEvent>> publishDefault(CatalogEvent event) {
        validateEvent(event);

        String key = safeKey(event.getItemId());
        CompletableFuture<SendResult<String, CatalogEvent>> future = kafkaTemplate.send(defaultTopic, key, event);

        attachLogging(future, defaultTopic, key, event);
        return future;
    }

    /**
     * Publish to a specific topic using event.getItemId() as the Kafka key.
     */
    public CompletableFuture<SendResult<String, CatalogEvent>> publishTo(String topic, CatalogEvent event) {
        Objects.requireNonNull(topic, "topic must not be null");
        validateEvent(event);

        String key = safeKey(event.getItemId());
        CompletableFuture<SendResult<String, CatalogEvent>> future = kafkaTemplate.send(topic, key, event);

        attachLogging(future, topic, key, event);
        return future;
    }

    /**
     * Publish to a specific topic with an explicit key (e.g., SKU or ID).
     */
    public CompletableFuture<SendResult<String, CatalogEvent>> publishWithKey(String topic, String key, CatalogEvent event) {
        Objects.requireNonNull(topic, "topic must not be null");
        Objects.requireNonNull(key, "key must not be null");
        validateEvent(event);

        String safeKey = safeKey(key);
        CompletableFuture<SendResult<String, CatalogEvent>> future = kafkaTemplate.send(topic, safeKey, event);

        attachLogging(future, topic, safeKey, event);
        return future;
    }

    /**
     * Publish with headers (e.g., schemaVersion, traceId). NOTE: Use
     * KafkaHeaders.KEY (version-safe), not KafkaHeaders.MESSAGE_KEY.
     */
    public CompletableFuture<SendResult<String, CatalogEvent>> publishWithHeaders(String topic, String key, CatalogEvent event, Map<String, String> headers) {
        Objects.requireNonNull(topic, "topic must not be null");
        Objects.requireNonNull(key, "key must not be null");
        validateEvent(event);

        MessageBuilder<CatalogEvent> mb = MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, safeKey(key)); // <-- FIXED

        if (headers != null && !headers.isEmpty()) {
            headers.forEach(mb::setHeader);
        }

        CompletableFuture<SendResult<String, CatalogEvent>> future = kafkaTemplate.send(mb.build());
        attachLogging(future, topic, key, event);
        return future;
    }

    // ---------- helpers ----------

    private void validateEvent(CatalogEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        Objects.requireNonNull(event.getEventId(), "eventId must not be null");
        Objects.requireNonNull(event.getType(), "eventType must not be null"); // ensure CatalogEvent has getType()
        Objects.requireNonNull(event.getPayload(), "payload must not be null");
        // itemId can be null in rare cases; we will replace with "UNKNOWN" for the
        // Kafka key.
    }

    private String safeKey(String key) {
        return (key == null || key.isBlank()) ? "UNKNOWN" : key;
    }

    private void attachLogging(CompletableFuture<SendResult<String, CatalogEvent>> future, String topic, String key, CatalogEvent event) {
        future.whenComplete((result, ex) -> {
            if (ex == null && result != null && result.getRecordMetadata() != null) {
                Log.info("Published topic={} partition={} offset={} key={} type={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        key,
                        event.getType());
            } else {
                Log.error("Kafka publish failed topic={} key={} type={} err={}",
                        topic,
                        key,
                        event != null ? event.getType() : "UNKNOWN",
                        ex != null ? ex.toString() : "unknown");
            }
        });
    }
}