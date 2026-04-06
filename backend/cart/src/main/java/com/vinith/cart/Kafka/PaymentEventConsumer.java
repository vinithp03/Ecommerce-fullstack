package com.vinith.cart.Kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinith.cart.ServiceLayer.OrderBinService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    private final OrderBinService orderBinService;
    private final ObjectMapper mapper = new ObjectMapper();

    public PaymentEventConsumer(OrderBinService orderBinService) {
        this.orderBinService = orderBinService;
    }

    @KafkaListener(topics = "payment-events", groupId = "cart-payment-group")
    public void onPaymentEvent(String rawJson) {
        try {
            JsonNode event = mapper.readTree(rawJson);
            String eventType = event.get("type").asText();
            Long orderId = Long.parseLong(event.get("key").asText());

            System.out.printf("PaymentEventConsumer: received %s for orderId=%d\n", eventType, orderId);

            if ("PAYMENT_SUCCESS".equals(eventType)) {
                orderBinService.clearBin(orderId);
                System.out.printf("PaymentEventConsumer: cleared bin for orderId=%d\n", orderId);
            }

        } catch (Exception e) {
            System.err.printf("PaymentEventConsumer error: %s\n", e.getMessage());
        }
    }
}
