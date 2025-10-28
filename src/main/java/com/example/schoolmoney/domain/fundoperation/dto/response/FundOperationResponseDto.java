package com.example.schoolmoney.domain.fundoperation.dto.response;

import com.example.schoolmoney.domain.child.dto.response.ChildShortInfoResponseDto;
import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.fundoperation.FundOperationType;
import com.example.schoolmoney.domain.parent.dto.response.ParentResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "fund_operation_id",
        "parent",
        "child",
        "fund_id",
        "amount_in_cents",
        "currency",
        "operation_type",
        "processed_at",
        "operation_status"
})
public class FundOperationResponseDto {

    @JsonProperty("fund_operation_id")
    private UUID fundOperationId;

    @JsonProperty("parent")
    private ParentResponseDto parent;

    @JsonProperty("child")
    private ChildShortInfoResponseDto child;

    @JsonProperty("fund_id")
    private UUID fundId;

    @JsonProperty("amount_in_cents")
    private long amountInCents;

    @JsonProperty("currency")
    private Currency currency;

    @JsonProperty("operation_type")
    private FundOperationType operationType;

    @JsonProperty("processed_at")
    private Instant processedAt;

    @JsonProperty("operation_status")
    private FinancialOperationStatus operationStatus;

}
