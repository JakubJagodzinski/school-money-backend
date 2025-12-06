package com.example.schoolmoney.domain.fundoperation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "note",
        "amount_in_cents"
})
public class WithdrawFromFundRequestDto {

    @JsonProperty("note")
    private String note;

    @JsonProperty("amount_in_cents")
    private Long amountInCents;

}
