package com.example.schoolmoney.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "message"
})
public class MessageResponseDto {

    @JsonProperty("message")
    private String message;

}
