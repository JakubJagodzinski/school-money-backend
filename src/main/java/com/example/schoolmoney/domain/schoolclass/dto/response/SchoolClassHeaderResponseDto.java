package com.example.schoolmoney.domain.schoolclass.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "school_class_id",
        "school_class_name",
        "school_class_year"
})
public class SchoolClassHeaderResponseDto {

    @JsonProperty("school_class_id")
    private UUID schoolClassId;

    @JsonProperty("school_class_name")
    private String schoolClassName;

    @JsonProperty("school_class_year")
    private String schoolClassYear;

}
