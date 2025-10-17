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
        "treasurer_id",
        "invitation_code",
        "school_class_name",
        "school_class_year"
})
public class SchoolClassResponseDto {

    @JsonProperty("school_class_id")
    private UUID schoolClassId;

    @JsonProperty("treasurer_id")
    private UUID treasurerId;

    @JsonProperty("invitation_code")
    private String invitationCode;

    @JsonProperty("school_class_name")
    private String schoolClassName;

    @JsonProperty("school_class_year")
    private String schoolClassYear;

}
