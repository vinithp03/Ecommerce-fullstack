package com.vinith.payment_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinith.payment_service.events.PaymentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {

    @Value("${payment.kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PaymentEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(PaymentEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, event.getKey(), message);
            System.out.println("Published event: " + event.getType() + " for key: " + event.getKey());
        } catch (Exception e) {
            System.err.println("Failed to publish payment event: " + e.getMessage());
        }
    }
}