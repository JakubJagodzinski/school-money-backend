package com.example.schoolmoney.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "refresh_token"
})
public class RefreshTokenRequestDto {

    @Schema(
            description = "Refresh token used to obtain a new access token",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    @NotBlank
    @JsonProperty("refresh_token")
    private String refreshToken;

}
