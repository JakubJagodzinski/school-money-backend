package com.example.schoolmoney.domain.wallet.dto.request;

import com.example.schoolmoney.finance.ProviderType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "provider_type",
        "amount_in_cents",
        "success_redirect_url",
        "cancel_redirect_url"
})
public class InitializeWalletTopUpRequestDto {

    @NotNull
    @JsonProperty("provider_type")
    private ProviderType providerType;

    @NotNull
    @JsonProperty("amount_in_cents")
    private Long amountInCents;

    @JsonProperty("success_redirect_url")
    private String successRedirectUrl;

    @JsonProperty("cancel_redirect_url")
    private String cancelRedirectUrl;

}
