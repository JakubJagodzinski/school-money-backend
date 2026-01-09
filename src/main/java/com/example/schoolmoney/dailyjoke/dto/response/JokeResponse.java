package com.example.schoolmoney.dailyjoke.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class JokeResponse {

    @JsonProperty("error")
    private boolean error;

    @JsonProperty("category")
    private String category;

    @JsonProperty("type")
    private String type;

    @JsonProperty("joke")
    private String joke;

    @JsonProperty("setup")
    private String setup;

    @JsonProperty("delivery")
    private String delivery;

}
