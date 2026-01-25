package com.example.schoolmoney.domain.financialoperation.dto.response;

import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.financialoperation.FinancialOperationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "started_at",
        "processed_at",
        "amount_in_cents",
        "operation_status",
        "operation_type"
})
public class FinancialOperationResponseDto {

    @JsonProperty("started_at")
    private Instant startedAt;

    @JsonProperty("processed_at")
    private Instant processedAt;

    @JsonProperty("amount_in_cents")
    private Long amountInCents;

    @JsonProperty("operation_status")
    private FinancialOperationStatus operationStatus;

    @JsonProperty("operation_type")
    private FinancialOperationType operationType;

}
