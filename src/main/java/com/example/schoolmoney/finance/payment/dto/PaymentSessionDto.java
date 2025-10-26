package com.example.schoolmoney.finance.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "session_id",
        "checkout_url"
})
public class PaymentSessionDto {

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("checkout_url")
    private String checkoutUrl;

}
