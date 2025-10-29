package com.example.schoolmoney.user;

import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.common.constants.messages.NotificationMessages;
import com.example.schoolmoney.common.constants.messages.PasswordMessages;
import com.example.schoolmoney.common.constants.messages.UserMessages;
import com.example.schoolmoney.common.dto.ApiErrorResponseDto;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.user.dto.request.ChangePasswordRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Change password"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Wrong password / password don't match",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            )
    })
    @CheckPermission(Permission.USER_PASSWORD_CHANGE)
    @PatchMapping("/users/password/change")
    public ResponseEntity<MessageResponseDto> changePassword(@Valid @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        userService.changePassword(changePasswordRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(PasswordMessages.PASSWORD_CHANGED_SUCCESSFULLY));
    }

    @PostMapping("/users/email/change/request")
    public ResponseEntity<MessageResponseDto> requestEmailChange(@RequestParam String newEmail) {
        userService.requestEmailChange(newEmail);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(UserMessages.EMAIL_CHANGE_REQUESTED));
    }

    @PostMapping("/users/email/change/confirm")
    public ResponseEntity<MessageResponseDto> confirmEmailChange(@RequestParam String token) {
        userService.confirmEmailChange(token);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(UserMessages.EMAIL_CHANGED_SUCCESSFULLY));
    }

    @PostMapping("/users/notifications/on")
    public ResponseEntity<MessageResponseDto> enableNotifications() {
        userService.enableNotifications();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(NotificationMessages.NOTIFICATIONS_ENABLED));
    }

    @PostMapping("/users/notifications/off")
    public ResponseEntity<MessageResponseDto> disableNotifications() {
        userService.disableNotifications();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(NotificationMessages.NOTIFICATIONS_DISABLED));
    }

}
