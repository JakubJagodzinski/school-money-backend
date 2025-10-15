package com.example.schoolmoney.domain.child.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@JsonPropertyOrder({
        "child_id",
        "parent_id",
        "school_class_id",
        "first_name",
        "last_name",
        "avatar_url",
        "birth_date"
})
public class ChildResponseDto {

    @JsonProperty("child_id")
    private UUID childId;

    @JsonProperty("parent_id")
    private UUID parentId;

    @JsonProperty("school_class_id")
    private UUID schoolClassId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

}
