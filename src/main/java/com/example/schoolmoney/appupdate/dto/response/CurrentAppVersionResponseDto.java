package com.example.schoolmoney.appupdate.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "current_version",
        "changelog"
})
public class CurrentAppVersionResponseDto {

    @JsonProperty("new_version")
    private String currentVersion;

    @JsonProperty("changelog")
    private List<String> changelog;

}
