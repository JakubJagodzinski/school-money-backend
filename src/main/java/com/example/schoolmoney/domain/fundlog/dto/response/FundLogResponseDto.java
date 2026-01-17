package com.example.schoolmoney.domain.fundlog.dto.response;

import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.fundlog.FundLogOperationType;
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
        "timestamp",
        "fund_title",
        "parent_full_name",
        "child_full_name",
        "amount_in_cents",
        "currency",
        "operation_type",
        "operation_status",
        "note"
})
public class FundLogResponseDto {

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("fund_title")
    private String fundTitle;

    @JsonProperty("parent_full_name")
    private String parentFullName;

    @JsonProperty("child_full_name")
    private String childFullName;

    @JsonProperty("amount_in_cents")
    private Long amountInCents;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("operation_type")
    private FundLogOperationType operationType;

    @JsonProperty("operation_status")
    private FinancialOperationStatus operationStatus;

    @JsonProperty("note")
    private String note;

}
