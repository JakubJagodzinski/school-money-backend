package com.example.schoolmoney.domain.wallet.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Currency;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "withdrawal_iban",
        "balance_in_cents",
        "currency"
})
public class WalletInfoResponseDto {

    @JsonProperty("withdrawal_iban")
    private String withdrawalIban;

    @JsonProperty("balance_in_cents")
    private long balanceInCents;

    @JsonProperty("currency")
    private Currency currency;

}
