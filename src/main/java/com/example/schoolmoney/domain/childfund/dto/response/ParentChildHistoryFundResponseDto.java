package com.example.schoolmoney.domain.childfund.dto.response;

import com.example.schoolmoney.domain.child.dto.response.ChildShortInfoResponseDto;
import com.example.schoolmoney.domain.fund.dto.response.FundResponseDto;
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
        "child",
        "child_status",
        "fund"
})
public class ParentChildHistoryFundResponseDto {

    @JsonProperty("child")
    private ChildShortInfoResponseDto child;

    @JsonProperty("child_status")
    private String childStatus;

    @JsonProperty("fund")
    private FundResponseDto fund;

}
