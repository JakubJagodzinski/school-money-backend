package com.example.schoolmoney.domain.walletoperation.dto.response;

import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.walletoperation.WalletOperationType;
import com.example.schoolmoney.finance.ProviderType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "wallet_operation_id",
        "external_operation_id",
        "provider_type",
        "iban",
        "amount_in_cents",
        "processed_at",
        "operation_type",
        "operation_status"
})
public class WalletOperationResponseDto {

    @JsonProperty("wallet_operation_id")
    private UUID walletOperationId;

    @JsonProperty("external_operation_id")
    private String externalOperationId;

    @JsonProperty("provider_type")
    private ProviderType providerType;

    @JsonProperty("iban")
    private String iban;

    @JsonProperty("amount_in_cents")
    private long amountInCents;

    @JsonProperty("processed_at")
    private Instant processedAt;

    @JsonProperty("operation_type")
    private WalletOperationType operationType;

    @JsonProperty("operation_status")
    private FinancialOperationStatus operationStatus;

}
