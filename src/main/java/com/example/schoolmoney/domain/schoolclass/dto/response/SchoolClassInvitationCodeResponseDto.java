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
        "invitation_code"
})
public class SchoolClassInvitationCodeResponseDto {

    @JsonProperty("school_class_id")
    private UUID schoolClassId;

    @JsonProperty("invitation_code")
    private String invitationCode;

}
