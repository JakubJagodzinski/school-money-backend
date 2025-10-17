package com.example.schoolmoney.domain.schoolclass.dto.request;

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
        "school_class_name",
        "school_class_year"
})
public class CreateSchoolClassRequestDto {

    @NotBlank
    @Size(min = 1, max = 50)
    @JsonProperty("school_class_name")
    private String schoolClassName;

    @NotBlank
    @Size(min = 1, max = 30)
    @JsonProperty("school_class_year")
    private String schoolClassYear;

}
