package com.example.schoolmoney.domain.fund.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;
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
        "school_class_id",
        "title",
        "description",
        "starts_at",
        "ends_at",
        "amount_per_child_in_cents"
})
public class CreateFundRequestDto {

    @NotNull
    @JsonProperty("school_class_id")
    private UUID schoolClassId;

    @NotBlank
    @Size(min = 3, max = 80)
    @JsonProperty("title")
    private String title;

    @Size(max = 1_000)
    @JsonProperty("description")
    private String description;

    @NotNull
    @JsonProperty("starts_at")
    private Instant startsAt;

    @NotNull
    @Future
    @JsonProperty("ends_at")
    private Instant endsAt;

    @NotNull
    @Min(0) // allow "free" funds
    @JsonProperty("amount_per_child_in_cents")
    private long amountPerChildInCents;

}
