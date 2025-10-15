package com.example.schoolmoney.domain.child.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@JsonPropertyOrder({
        "parent_id",
        "first_name",
        "last_name",
        "birth_date"
})
public class CreateChildRequestDto {

    @JsonProperty("parent_id")
    private UUID parentId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

}
