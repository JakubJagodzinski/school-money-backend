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
        "currency",
        "balance_in_cents",
        "withdrawal_iban"
})
public class WalletInfoResponseDto {

    @JsonProperty("currency")
    private Currency currency;

    @JsonProperty("balance_in_cents")
    private long balanceInCents;

    @JsonProperty("withdrawal_iban")
    private String withdrawalIban;

}
