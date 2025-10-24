package com.example.schoolmoney.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "current_password",
        "new_password",
        "confirmation_password"
})
public class ChangePasswordRequestDto {

    @Schema(
            description = "Current password of the user",
            example = "oldPassword12345",
            maxLength = 128
    )
    @NotBlank
    @Size(max = 128)
    @JsonProperty("current_password")
    private String currentPassword;

    @Schema(
            description = "New password the user wants to set",
            example = "newPassword12345",
            maxLength = 128
    )
    @NotBlank
    @Size(max = 128)
    @JsonProperty("new_password")
    private String newPassword;

    @Schema(
            description = "Confirmation of the new password, must match exactly with new_password value",
            example = "newPassword12345",
            maxLength = 128
    )
    @NotBlank
    @Size(max = 128)
    @JsonProperty("confirmation_password")
    private String confirmationPassword;

}
