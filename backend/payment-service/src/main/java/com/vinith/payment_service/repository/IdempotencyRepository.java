
package com.vinith.payment_service.repository;

import com.vinith.payment_service.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyRepository
        extends JpaRepository<IdempotencyRecord, String> {

    Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);

    @Modifying
    @Query(value = """
        INSERT IGNORE INTO idempotency_records
            (idempotency_key, order_id, status, created_at)
        VALUES (:key, :orderId, 'PENDING', NOW())
        """, nativeQuery = true)
    int claimIdempotencyKey(
            @Param("key") String key,
            @Param("orderId") Long orderId
    );
}
