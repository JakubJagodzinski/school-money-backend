package com.example.schoolmoney.dailyjoke.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class JokeResponseDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("setup")
    private String setup;

    @JsonProperty("punchline")
    private String punchline;

}
