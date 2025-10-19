package com.example.schoolmoney.domain.parent.dto.request;

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
        "first_name",
        "last_name"
})
public class UpdateParentRequestDto {

    @NotBlank
    @Size(min = 1, max = 30)
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 50)
    @JsonProperty("last_name")
    private String lastName;

}
