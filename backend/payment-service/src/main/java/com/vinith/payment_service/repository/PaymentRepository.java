package com.vinith.payment_service.repository;

import com.vinith.payment_service.entity.Payment;
import com.vinith.payment_service.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    @Modifying
    @Query(value = """
        INSERT INTO payments
            (order_id, user_id, amount, status, payment_method, created_at, updated_at)
        VALUES
            (:orderId, :userId, :amount, 'PENDING', :paymentMethod, NOW(), NOW())
        ON DUPLICATE KEY UPDATE order_id = order_id
        """, nativeQuery = true)
    int reservePayment(
            @Param("orderId") Long orderId,
            @Param("userId") Long userId,
            @Param("amount") BigDecimal amount,
            @Param("paymentMethod") String paymentMethod
    );

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
        UPDATE Payment p
           SET p.status = :pending,
               p.failureReason = null,
               p.amount = :amount,
               p.paymentMethod = :paymentMethod
         WHERE p.orderId = :orderId
           AND p.status = :failed
    """)
    int claimFailedPaymentForRetry(
            @Param("orderId") Long orderId,
            @Param("amount") BigDecimal amount,
            @Param("paymentMethod") String paymentMethod,
            @Param("pending") PaymentStatus pending,
            @Param("failed") PaymentStatus failed
    );
}