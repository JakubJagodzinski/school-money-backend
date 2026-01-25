package com.example.schoolmoney.domain.wallet.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "amount_in_cents",
        "iban"
})
public class PerformWalletWithdrawalRequestDto {

    @NotNull
    @JsonProperty("amount_in_cents")
    private Long amountInCents;

    @JsonProperty("iban")
    private String iban;

}
