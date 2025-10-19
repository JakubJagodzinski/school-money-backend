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
        "email",
        "redirectUrl"
})
public class CreateResetPasswordTokenRequestDto {

    @JsonProperty("email")
    private String email;

    @JsonProperty("redirectUrl")
    private String redirectUrl;

}
