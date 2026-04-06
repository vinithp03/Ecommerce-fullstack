package com.vinith.payment_service.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {
    private String eventId;
    private String type;
    private String key;
    private String payload;
    private long timestamp;
}