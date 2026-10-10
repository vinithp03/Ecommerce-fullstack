package com.vinith.payment_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinith.payment_service.client.OrderClient;
import com.vinith.payment_service.client.PaymentGatewayClient;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final OrderClient orderClient;
    private final PaymentEventProducer eventProducer;
    private final ObjectMapper objectMapper;
    private final PaymentGatewayClient paymentGatewayClient;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentService(
            PaymentRepository paymentRepository,
            IdempotencyRepository idempotencyRepository,
            OrderClient orderClient,
            PaymentEventProducer eventProducer,
            ObjectMapper objectMapper,
            PaymentGatewayClient paymentGatewayClient,
            ApplicationEventPublisher eventPublisher) {

        this.paymentRepository = paymentRepository;
        this.idempotencyRepository = idempotencyRepository;
        this.orderClient = orderClient;
        this.eventProducer = eventProducer;
        this.objectMapper = objectMapper;
        this.paymentGatewayClient = paymentGatewayClient;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PaymentResponse processPayment(
            Long orderId,
            PaymentRequest request,
            String idempotencyKey) {

        // --------------------------------------------------
        // Step 1: Atomically claim idempotency key
        // --------------------------------------------------

        int claimed = idempotencyRepository.claimIdempotencyKey(
                idempotencyKey,
                orderId
        );

        System.out.println("IDEMPOTENCY CLAIM: key=" + idempotencyKey + ", orderId=" + orderId + ", affectedRows=" + claimed);

        if (claimed == 0) {

            // Another request already owns this idempotency key
            IdempotencyRecord existing =
                    idempotencyRepository.findByIdempotencyKey(idempotencyKey)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Idempotency record not found"
                                    )
                            );

            if (existing.getStatus() == PaymentStatus.SUCCESS ||
                    existing.getStatus() == PaymentStatus.FAILED) {

                return deserializeResponse(
                        existing.getResponseBody()
                );
            }

            // PENDING means another request is currently processing it
            throw new RuntimeException(
                    "Payment is already being processed. Please try again."
            );
        }

        // --------------------------------------------------
        // Step 2: Validate order via Order Service
        // --------------------------------------------------

        OrderResponse order = orderClient.getOrder(orderId);

        // Order not found
        if (order == null) {
            throw new RuntimeException(
                    "Order not found: " + orderId
            );
        }

        // Order service unavailable
        if (order.isServiceDown()) {
            throw new RuntimeException(
                    "Order service unavailable. Try later."
            );
        }

        // Order must still be CREATED
        if (!"CREATED".equals(order.getStatus())) {
            throw new RuntimeException(
                    "Invalid order state: " + order.getStatus()
            );
        }

        // --------------------------------------------------
        // Step 3: Atomically reserve payment
        // --------------------------------------------------

        String paymentMethod =
                request.getPaymentMethod() != null
                        ? request.getPaymentMethod()
                        : "MOCK";

        int reserved = paymentRepository.reservePayment(
                orderId,
                order.getUserId(),
                order.getTotalAmount(),
                paymentMethod
        );

        Payment payment;

        if (reserved == 1) {

            // We successfully created the PENDING payment.
            payment = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Payment reservation not found"
                            )
                    );

        } else {

            // Someone already created a payment for this order.
            payment = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Payment record not found"
                            )
                    );

            // --------------------------------------------------
            // CASE 1: Payment already SUCCESS
            // --------------------------------------------------

            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                return toResponse(payment);
            }

            // --------------------------------------------------
            // CASE 2: Payment currently PENDING
            // --------------------------------------------------

            if (payment.getStatus() == PaymentStatus.PENDING) {
                throw new RuntimeException(
                        "Payment is already being processed. Please try again."
                );
            }

            // --------------------------------------------------
            // CASE 3: Payment FAILED
            // Atomically claim FAILED -> PENDING
            // --------------------------------------------------

            int retryClaimed =
                    paymentRepository.claimFailedPaymentForRetry(
                            orderId,
                            order.getTotalAmount(),
                            paymentMethod,
                            PaymentStatus.PENDING,
                            PaymentStatus.FAILED
                    );

            if (retryClaimed == 0) {

                /*
                 * Another request won the FAILED -> PENDING race.
                 *
                 * Therefore this request does NOT own the retry
                 * and must NOT call the payment gateway.
                 */
                throw new RuntimeException(
                        "Payment retry is already being processed. Please try again."
                );
            }

            /*
             * We successfully changed:
             *
             * FAILED -> PENDING
             *
             * Therefore this request owns the gateway call.
             */
            payment = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Payment record not found after retry claim"
                            )
                    );
        }

        // --------------------------------------------------
        // Step 4: Payment Gateway
        // --------------------------------------------------

        boolean paymentSuccess =
                paymentGatewayClient.processPayment(
                        orderId,
                        order.getTotalAmount()
                );

        // --------------------------------------------------
        // Step 5: Update payment
        // --------------------------------------------------

        payment.setStatus(
                paymentSuccess
                        ? PaymentStatus.SUCCESS
                        : PaymentStatus.FAILED
        );

        payment.setFailureReason(
                paymentSuccess
                        ? null
                        : "Payment declined by mock gateway"
        );

        Payment saved = paymentRepository.save(payment);

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

        // --------------------------------------------------
        // Step 8: Prepare Kafka event
        // --------------------------------------------------

        String eventType =
                paymentSuccess
                        ? "PAYMENT_SUCCESS"
                        : "PAYMENT_FAILED";

        String payload = toJson(
                Map.of(
                        "orderId", orderId,
                        "userId", order.getUserId(),
                        "amount", order.getTotalAmount(),
                        "status", payment.getStatus(),
                        "reason",
                        payment.getFailureReason() != null
                                ? payment.getFailureReason()
                                : ""
                )
        );

        PaymentEvent event = new PaymentEvent(
                UUID.randomUUID().toString(),
                eventType,
                String.valueOf(orderId),
                payload,
                System.currentTimeMillis()
        );

        /*
         * Publish Spring application event.
         *
         * Actual Kafka publishing happens only AFTER
         * the DB transaction successfully commits.
         */
        eventPublisher.publishEvent(
                new PaymentEventPublished(event)
        );

        return response;
    }

    // --------------------------------------------------
    // Kafka: Publish only AFTER DB transaction commits
    // --------------------------------------------------

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onPaymentEvent(
            PaymentEventPublished event) {

        eventProducer.publish(
                event.paymentEvent()
        );
    }

    // --------------------------------------------------
    // Helper: Payment -> PaymentResponse
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

    // --------------------------------------------------
    // Helper: JSON -> PaymentResponse
    // --------------------------------------------------

    private PaymentResponse deserializeResponse(String json) {

        try {

            return objectMapper.readValue(
                    json,
                    PaymentResponse.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to deserialize idempotency response",
                    e
            );
        }
    }

    // --------------------------------------------------
    // Helper: Object -> JSON
    // --------------------------------------------------

    private String toJson(Object value) {

        try {

            return objectMapper.writeValueAsString(value);

        } catch (Exception e) {

            return "{}";
        }
    }

    // --------------------------------------------------
    // Spring Application Event
    // --------------------------------------------------

    public record PaymentEventPublished(
            PaymentEvent paymentEvent
    ) {
    }
}