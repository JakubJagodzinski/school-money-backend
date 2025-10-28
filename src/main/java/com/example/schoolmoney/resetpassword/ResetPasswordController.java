package com.example.schoolmoney.resetpassword;

import com.example.schoolmoney.common.constants.messages.ResetPasswordTokenMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.resetpassword.dto.request.RequestPasswordResetRequestDto;
import com.example.schoolmoney.resetpassword.dto.request.ResetPasswordRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;

    @PostMapping("/password-reset/request")
    public ResponseEntity<MessageResponseDto> requestPasswordReset(@Valid @RequestBody RequestPasswordResetRequestDto requestPasswordResetRequestDto) {
        resetPasswordService.requestPasswordReset(requestPasswordResetRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(ResetPasswordTokenMessages.PASSWORD_RESET_REQUESTED));
    }

    @PostMapping("/password-reset")
    public ResponseEntity<MessageResponseDto> resetPassword(@Valid @RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        resetPasswordService.resetPassword(resetPasswordRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(ResetPasswordTokenMessages.PASSWORD_RESET_SUCCESSFULLY));
    }

}
