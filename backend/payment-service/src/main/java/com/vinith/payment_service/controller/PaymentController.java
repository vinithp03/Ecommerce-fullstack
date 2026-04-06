package com.vinith.payment_service.controller;

import com.vinith.payment_service.dto.PaymentRequest;
import com.vinith.payment_service.dto.PaymentResponse;
import com.vinith.payment_service.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/payment/v1")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}/pay")
    public ResponseEntity<?> processPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        try {
            // Validate idempotency key present
            if (idempotencyKey == null || idempotencyKey.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("Idempotency-Key header is required");
            }

            PaymentResponse response = paymentService.processPayment(
                    orderId, request, idempotencyKey);

            return ResponseEntity.ok(response);

        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}