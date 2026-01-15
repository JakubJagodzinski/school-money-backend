package com.example.schoolmoney.domain.fund.dto.response;

import com.example.schoolmoney.domain.fund.FundStatus;
import com.example.schoolmoney.domain.schoolclass.dto.response.SchoolClassHeaderResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "fund_id",
        "author_id",
        "school_class",
        "title",
        "logo_url",
        "description",
        "created_at",
        "starts_at",
        "ends_at",
        "amount_per_child_in_cents",
        "iban",
        "fund_status",
        "fund_progress",
        "children"
})
public class FundWithChildrenResponseDto {

    @JsonProperty("fund_id")
    private UUID fundId;

    @JsonProperty("author_id")
    private UUID authorId;

    @JsonProperty("school_class")
    private SchoolClassHeaderResponseDto schoolClass;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("created_at")
    private Instant createdAt;

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

    @JsonProperty("fund_progress")
    private double fundProgress;

    @JsonProperty("children")
    private List<FundChildStatusWithoutParentResponseDto> children;

}
