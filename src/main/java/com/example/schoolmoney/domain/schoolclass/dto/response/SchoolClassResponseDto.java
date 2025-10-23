package com.example.schoolmoney.domain.schoolclass.dto.response;

import com.example.schoolmoney.domain.parent.dto.response.ParentResponseDto;
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
        "treasurer",
        "invitation_code",
        "school_class_name",
        "school_class_year"
})
public class SchoolClassResponseDto {

    @JsonProperty("school_class_id")
    private UUID schoolClassId;

    @JsonProperty("treasurer")
    private ParentResponseDto treasurer;

    @JsonProperty("invitation_code")
    private String invitationCode;

    @JsonProperty("school_class_name")
    private String schoolClassName;

    @JsonProperty("school_class_year")
    private String schoolClassYear;

    @JsonProperty("number_of_children")
    private long numberOfChildren;

    @JsonProperty("number_of_active_funds")
    private long numberOfActiveFunds;

}
