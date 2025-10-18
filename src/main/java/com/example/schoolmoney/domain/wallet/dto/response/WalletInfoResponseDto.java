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
        "withdrawal_iban"
})
public class WalletInfoResponseDto {

    @JsonProperty("balance_in_cents")
    private Long balanceInCents;

    @JsonProperty("withdrawal_iban")
    private String withdrawalIban;

}
