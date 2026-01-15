package com.example.schoolmoney.dailyjoke.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "joke"
})
public class RandomJokeResponseDto {

    @JsonProperty("joke")
    private String joke;

}
