package com.example.schoolmoney.domain.fund.dto.response;

import com.example.schoolmoney.domain.fund.FundStatus;
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
        "fund_id",
        "author_id",
        "school_class_id",
        "title",
        "logo_url",
        "description",
        "starts_at",
        "ends_at",
        "amount_per_child_in_cents",
        "iban",
        "fund_status"
})
public class FundResponseDto {

    @JsonProperty("fund_id")
    private UUID fundId;

    @JsonProperty("author_id")
    private UUID authorId;

    @JsonProperty("school_class_id")
    private UUID schoolClassId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("description")
    private String description;

    @JsonProperty("starts_at")
    private Instant startsAt;

    @JsonProperty("ends_at")
    private Instant endsAt;

    @JsonProperty("amount_per_child_in_cents")
    private long amountPerChildInCents;

    @JsonProperty("iban")
    private String iban;

    @JsonProperty("fund_status")
    private FundStatus fundStatus;

}
