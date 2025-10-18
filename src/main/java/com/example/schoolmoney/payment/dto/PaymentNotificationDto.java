package com.example.schoolmoney.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "external_payment_id",
        "event_type",
        "user_id",
        "amount_in_cents",
        "currency",
        "raw_event"
})
public class PaymentNotificationDto {

    @JsonProperty("external_payment_id")
    private String externalPaymentId;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("amount_in_cents")
    private long amountInCents;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("raw_event")
    private Object rawEvent;

}
