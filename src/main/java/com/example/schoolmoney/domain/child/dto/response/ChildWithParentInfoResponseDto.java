package com.example.schoolmoney.domain.child.dto.response;

import com.example.schoolmoney.domain.parent.dto.response.ParentPublicResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "child_id",
        "parent",
        "first_name",
        "last_name",
        "avatar_url",
        "birth_date"
})
public class ChildWithParentInfoResponseDto {

    @JsonProperty("child_id")
    private UUID childId;

    @JsonProperty("parent")
    private ParentPublicResponseDto parent;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

}
