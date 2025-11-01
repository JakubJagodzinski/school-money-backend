package com.example.schoolmoney.domain.fund.dto.response;

import com.example.schoolmoney.domain.child.dto.response.ChildWithParentInfoResponseDto;
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
public class FundChildStatusResponseDto {

    @JsonProperty("child")
    private ChildWithParentInfoResponseDto child;

    @JsonProperty("status")
    private FundChildStatus status;

}
