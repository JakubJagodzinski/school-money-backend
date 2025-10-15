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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "first_name",
        "last_name",
        "email",
        "password"
})
public class RegisterRequestDto {

    @Schema(
            description = "User's first name",
            example = "John",
            maxLength = 30
    )
    @NotBlank
    @Size(max = 30)
    @JsonProperty("first_name")
    private String firstName;

    @Schema(
            description = "User's last name",
            example = "Doe",
            maxLength = 50
    )
    @NotBlank
    @Size(max = 50)
    @JsonProperty("last_name")
    private String lastName;

    @Schema(
            description = "User's email address",
            example = "john.doe@example.com",
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
