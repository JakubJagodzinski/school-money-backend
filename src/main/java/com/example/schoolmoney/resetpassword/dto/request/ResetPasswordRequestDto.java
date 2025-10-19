package com.example.schoolmoney.resetpassword.dto.request;

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
        "reset_password_token",
        "new_password"
})
public class ResetPasswordRequestDto {

    @JsonProperty("reset_password_token")
    private String resetPasswordToken;

    @JsonProperty("new_password")
    private String newPassword;

}
