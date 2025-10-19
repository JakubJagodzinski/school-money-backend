package com.example.schoolmoney.domain.fund.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "title",
        "description"
})
public class UpdateFundRequestDto {

    @NotBlank
    @Size(min = 3, max = 80)
    @JsonProperty("title")
    private String title;

    @Size(max = 1_000)
    @JsonProperty("description")
    private String description;

}
