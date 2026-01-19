package com.example.schoolmoney.appupdate.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.List;

@Getter
@JsonPropertyOrder({
        "new_version",
        "changelog"
})
public class CreateAppUpdateRequestDto {

    @JsonProperty("new_version")
    private String newVersion;

    @JsonProperty("changelog")
    private List<String> changelog;

}
