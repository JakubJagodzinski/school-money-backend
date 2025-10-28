package com.example.schoolmoney.auth;

import com.example.schoolmoney.auth.dto.request.AuthenticationRequestDto;
import com.example.schoolmoney.auth.dto.request.RefreshTokenRequestDto;
import com.example.schoolmoney.auth.dto.request.RegisterRequestDto;
import com.example.schoolmoney.auth.dto.response.AuthenticationResponseDto;
import com.example.schoolmoney.auth.dto.response.RefreshTokenResponseDto;
import com.example.schoolmoney.common.constants.messages.EmailMessages;
import com.example.schoolmoney.common.constants.messages.UserMessages;
import com.example.schoolmoney.common.dto.ApiErrorResponseDto;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.user.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Register new parent account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email is already taken",
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
    @PostMapping("/register")
    public ResponseEntity<MessageResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        authenticationService.register(registerRequestDto, Role.PARENT);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponseDto(UserMessages.IF_NOT_ALREADY_REGISTERED_ACCOUNT_CREATED_SUCCESSFULLY));
    }

    @Operation(
            summary = "Verify account",
            parameters = {
                    @Parameter(
                            name = "token",
                            description = "Verification token",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", example = "ce47e175-1a3f-43ae-9e78-1d83c18c1a12")
                    ),
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account verified successfully",
                    content = @Content(
                            mediaType = "text/html",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Failed to authorize account",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            )
    })
    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam String token) {
        authenticationService.verifyAccount(token);

        String html = "<html>" +
                "<head><title>Account verification complete</title></head>" +
                "<body>" +
                "<h1>Your account is now verified, welcome to SchoolMoney!</h1>" +
                "</body>" +
                "</html>";

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @Operation(
            summary = "Resend verification email",
            parameters = {
                    @Parameter(
                            name = "email",
                            description = "User email",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", example = "john.doe@dietitian_plus.com")
                    ),
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification email resend",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Account already verified",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            )
    })
    @PostMapping("/verify/resend")
    public ResponseEntity<MessageResponseDto> resendVerificationEmail(@RequestParam String email) {
        authenticationService.sendVerificationEmail(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(EmailMessages.VERIFICATION_EMAIL_RESEND));
    }

    @Operation(
            summary = "Authenticate with email and password"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Wrong username or password",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(@Valid @RequestBody AuthenticationRequestDto authenticationRequestDto) {
        AuthenticationResponseDto authenticationResponseDto = authenticationService.authenticate(authenticationRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authenticationResponseDto);
    }

    @Operation(
            summary = "Use refresh token to generate new access and refresh token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tokens refreshed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RefreshTokenResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Provided refresh token is invalid or expired",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/token/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        RefreshTokenResponseDto refreshTokenResponseDto = authenticationService.refreshToken(refreshTokenRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(refreshTokenResponseDto);
    }

}
