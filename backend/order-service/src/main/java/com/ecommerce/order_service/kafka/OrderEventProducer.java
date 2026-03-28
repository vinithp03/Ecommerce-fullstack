package com.ecommerce.order_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {
    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.order-events}")
    private String orderEventsTopic;

    public OrderEventProducer(KafkaTemplate<String,String> kafkatemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkatemplate;
        this.objectMapper = objectMapper;
    }

    public void publishOrderEvent(OrderEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(orderEventsTopic, event.getEventType(), payload);
            System.out.println("Published event: " + event.getEventType() + " for orderId: " + event.getOrderId());
        } catch (Exception e) {
            System.err.println("Failed to publish order event: " + e.getMessage());
        }
    }

}
