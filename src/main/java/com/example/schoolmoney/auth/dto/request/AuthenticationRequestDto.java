package com.example.schoolmoney.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonPropertyOrder({
        "email",
        "password"
})
public class AuthenticationRequestDto {

    @Schema(
            description = "User's email address used for login",
            example = "user@example.com",
            maxLength = 254
    )
    @Email(message = "Invalid email format")
    @NotBlank
    @Size(max = 254)
    @JsonProperty("email")
    private String email;

    @Schema(
            description = "User's password",
            example = "password12345",
            maxLength = 128
    )
    @NotBlank
    @Size(max = 128)
    @JsonProperty("password")
    private String password;

}
