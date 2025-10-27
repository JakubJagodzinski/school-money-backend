package com.example.schoolmoney.finance.payout.dto;

import com.example.schoolmoney.finance.ProviderType;
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
        "provider_type",
        "external_payout_id",
        "event_type",
        "user_id",
        "operation_id",
        "amount_in_cents",
        "currency",
        "raw_event"
})
public class PayoutNotificationDto {

    @JsonProperty("provider_type")
    private ProviderType providerType;

    @JsonProperty("external_payout_id")
    private String externalPayoutId;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("operation_id")
    private UUID operationId;

    @JsonProperty("amount_in_cents")
    private long amountInCents;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("raw_event")
    private Object rawEvent;

}
