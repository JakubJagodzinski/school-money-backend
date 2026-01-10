package com.example.schoolmoney.domain.fund.dto.response;

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
        "progress_percentage",
        "current_amount_in_cents",
        "target_amount_in_cents",
        "remaining_amount_in_cents",
        "participating_children_count",
        "paid_children_count",
        "unpaid_children_count",
        "ignored_children_count"
})
public class FundProgressResponseDto {

    @JsonProperty("progress_percentage")
    private Double progressPercentage;

    @JsonProperty("current_amount_in_cents")
    private Long currentAmountInCents;

    @JsonProperty("target_amount_in_cents")
    private Long targetAmountInCents;

    @JsonProperty("remaining_amount_in_cents")
    private Long remainingAmountInCents;

    @JsonProperty("participating_children_count")
    private Long participatingChildrenCount;

    @JsonProperty("paid_children_count")
    private Long paidChildrenCount;

    @JsonProperty("unpaid_children_count")
    private Long unpaidChildrenCount;

    @JsonProperty("ignored_children_count")
    private Long ignoredChildrenCount;

}
