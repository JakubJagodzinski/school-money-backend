package com.example.schoolmoney.domain.wallet.dto.response;

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
        "balance_in_cents",
        "currency"
})
public class WalletBalanceResponseDto {

    @JsonProperty("balance_in_cents")
    private long balanceInCents;

    @JsonProperty("currency")
    private String currency;

}
