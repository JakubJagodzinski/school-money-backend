package com.example.schoolmoney.domain.fund.dto.response;

import com.example.schoolmoney.domain.child.dto.response.ChildShortInfoResponseDto;
import com.example.schoolmoney.domain.fund.FundChildStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonPropertyOrder({
        "child",
        "status"
})
public class FundChildStatusWithoutParentResponseDto {

    @JsonProperty("child")
    private ChildShortInfoResponseDto child;

    @JsonProperty("status")
    private FundChildStatus status;

}
