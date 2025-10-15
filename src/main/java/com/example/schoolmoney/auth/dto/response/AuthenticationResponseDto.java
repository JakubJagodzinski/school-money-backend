package com.example.schoolmoney.auth.dto.response;

import com.example.schoolmoney.user.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "user_id",
        "role",
        "first_name",
        "last_name",
        "access_token",
        "refresh_token"
})
public class AuthenticationResponseDto {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("role")
    private Role role;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

}
