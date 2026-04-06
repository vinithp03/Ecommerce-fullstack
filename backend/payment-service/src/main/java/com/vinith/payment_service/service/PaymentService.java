package com.vinith.payment_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinith.payment_service.dto.OrderResponse;
import com.vinith.payment_service.dto.PaymentRequest;
import com.vinith.payment_service.dto.PaymentResponse;
import com.vinith.payment_service.entity.IdempotencyRecord;
import com.vinith.payment_service.entity.Payment;
import com.vinith.payment_service.entity.PaymentStatus;
import com.vinith.payment_service.events.PaymentEvent;
import com.vinith.payment_service.kafka.PaymentEventProducer;
import com.vinith.payment_service.repository.IdempotencyRepository;
import com.vinith.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final OrderClient orderClient;
    private final PaymentEventProducer eventProducer;
    private final ObjectMapper objectMapper;

    public PaymentService(PaymentRepository paymentRepository,
                          IdempotencyRepository idempotencyRepository,
                          OrderClient orderClient,
                          PaymentEventProducer eventProducer,
                          ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.idempotencyRepository = idempotencyRepository;
        this.orderClient = orderClient;
        this.eventProducer = eventProducer;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PaymentResponse processPayment(Long orderId,
                                          PaymentRequest request,
                                          String idempotencyKey) {
        // --------------------------------------------------
        // Step 1: Idempotency check
        // --------------------------------------------------
        Optional<IdempotencyRecord> existing =
                idempotencyRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            // Request already processed — replay stored response
            return deserializeResponse(existing.get().getResponseBody());
        }

        // --------------------------------------------------
        // Step 2: Validate order via Order Service
        // --------------------------------------------------
        OrderResponse order = orderClient.getOrder(orderId);

        if (order == null) {
            throw new RuntimeException("Order not found: " + orderId);
        }

        if (!"CREATED".equals(order.getStatus())) {
            throw new RuntimeException("Order is not in CREATED state. Current state: "
                    + order.getStatus());
        }

        // --------------------------------------------------
        // Step 3: Mock payment gateway
        // --------------------------------------------------
        boolean paymentSuccess = mockGateway();

        // --------------------------------------------------
        // Step 4: Save payment record
        // --------------------------------------------------
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setUserId(order.getUserId());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(
                request.getPaymentMethod() != null
                        ? request.getPaymentMethod()
                        : "MOCK");
        payment.setStatus(paymentSuccess
                ? PaymentStatus.SUCCESS
                : PaymentStatus.FAILED);
        payment.setFailureReason(paymentSuccess
                ? null
                : "Payment declined by mock gateway");

        Payment saved = paymentRepository.save(payment);

        // --------------------------------------------------
        // Step 5: Publish Kafka event
        // --------------------------------------------------
        String eventType = paymentSuccess ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED";

        String payload = toJson(Map.of(
                "orderId", orderId,
                "userId", order.getUserId(),
                "amount", order.getTotalAmount(),
                "status", payment.getStatus(),
                "reason", payment.getFailureReason() != null
                        ? payment.getFailureReason()
                        : ""
        ));

        PaymentEvent event = new PaymentEvent(
                UUID.randomUUID().toString(),
                eventType,
                String.valueOf(orderId), // Kafka key = orderId
                payload,
                System.currentTimeMillis()
        );

        eventProducer.publish(event);

        // --------------------------------------------------
        // Step 6: Build response
        // --------------------------------------------------
        PaymentResponse response = toResponse(saved);

        // --------------------------------------------------
        // Step 7: Save idempotency record
        // --------------------------------------------------
        IdempotencyRecord record = new IdempotencyRecord();
        record.setIdempotencyKey(idempotencyKey);
        record.setOrderId(orderId);
        record.setStatus(saved.getStatus());
        record.setResponseBody(toJson(response));
        idempotencyRepository.save(record);

        return response;
    }

    // --------------------------------------------------
    // Mock gateway — 90% success rate
    // --------------------------------------------------
    private boolean mockGateway() {
        return Math.random() > 0.1;
    }

    // --------------------------------------------------
    // Helpers
    // --------------------------------------------------
    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setOrderId(payment.getOrderId());
        response.setUserId(payment.getUserId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setFailureReason(payment.getFailureReason());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }

    private PaymentResponse deserializeResponse(String json) {
        try {
            return objectMapper.readValue(json, PaymentResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize idempotency response", e);
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "{}";
        }
    }
}