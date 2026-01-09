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
        "fund"
})
public class ParentChildUnpaidFundResponseDto {

    @JsonProperty("child")
    private ChildShortInfoResponseDto child;

    @JsonProperty("fund")
    private FundResponseDto fund;

}
