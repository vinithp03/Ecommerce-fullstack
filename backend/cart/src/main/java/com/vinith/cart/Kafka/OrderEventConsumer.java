package com.vinith.cart.Kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinith.cart.ServiceLayer.OrderBinService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private final OrderBinService orderBinService;
    private final ObjectMapper mapper = new ObjectMapper();

    public OrderEventConsumer(OrderBinService orderBinService) {
        this.orderBinService = orderBinService;
    }

    @KafkaListener(topics = "${order.kafka.topic}", groupId = "cart-service")
    public void onOrderEvent(String rawJson) {
        try {
            JsonNode event = mapper.readTree(rawJson);

            String eventType = event.get("eventType").asText();
            Long orderId = event.get("orderId").asLong();
            Long userId = event.get("userId").asLong();

            System.out.printf("OrderEventConsumer: received %s for orderId=%d userId=%d\n",
                    eventType, orderId, userId);

            switch (eventType) {
                case "ORDER_CREATED" -> orderBinService.moveToBin(orderId, userId);
                case "ORDER_CANCELLED" -> orderBinService.restoreFromBin(orderId, userId);
                case "PAYMENT_SUCCESS" -> orderBinService.clearBin(orderId);
                default -> System.out.printf("OrderEventConsumer: unknown eventType=%s\n", eventType);
            }

        } catch (Exception e) {
            System.err.printf("OrderEventConsumer error: %s\n", e.getMessage());
        }
    }
}