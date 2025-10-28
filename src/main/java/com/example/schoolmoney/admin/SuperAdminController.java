package com.example.schoolmoney.admin;

import com.example.schoolmoney.auth.AuthenticationService;
import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.auth.dto.request.RegisterRequestDto;
import com.example.schoolmoney.common.constants.messages.UserMessages;
import com.example.schoolmoney.common.dto.ApiErrorResponseDto;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.user.Permission;
import com.example.schoolmoney.user.Role;
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
public class SuperAdminController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Create new admin account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Admin account created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email is already taken / invalid user role",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Email address domain not allowed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponseDto.class)
                    )
            )
    })
    @CheckPermission(Permission.ADMIN_ACCOUNT_CREATE)
    @PostMapping("/admins")
    public ResponseEntity<MessageResponseDto> createAdminAccount(@RequestParam Role role, @Valid @RequestBody RegisterRequestDto registerRequestDto) throws IllegalArgumentException {
        if (!role.isAdminRole()) {
            throw new IllegalArgumentException(UserMessages.USER_ROLE_IS_NOT_ADMIN_ROLE);
        }

        authenticationService.register(registerRequestDto, role);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponseDto(UserMessages.IF_NOT_ALREADY_REGISTERED_ACCOUNT_CREATED_SUCCESSFULLY));
    }

}
