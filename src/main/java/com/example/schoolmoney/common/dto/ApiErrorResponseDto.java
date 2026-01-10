package com.example.schoolmoney.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonPropertyOrder({
        "status",
        "message",
        "error_id",
        "timestamp",
        "errors"
})
public class ApiErrorResponseDto {

    @JsonProperty("status")
    private final int status;

    @JsonProperty("message")
    private final String message;

    @JsonProperty("error_id")
    private UUID errorId;

    @Builder.Default
    @JsonProperty("timestamp")
    private final Instant timestamp = Instant.now();

    @JsonProperty("errors")
    private final Map<String, String> errors;

}
