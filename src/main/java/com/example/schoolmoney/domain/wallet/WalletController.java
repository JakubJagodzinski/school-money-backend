package com.example.schoolmoney.domain.wallet;

import com.example.schoolmoney.common.constants.messages.WalletMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.domain.wallet.dto.response.WalletBalanceResponseDto;
import com.example.schoolmoney.domain.wallet.dto.response.WalletInfoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class WalletController {

    private final WalletService walletService;

    @Operation(
            summary = "Get wallet information",
            description = """
                    Retrieves detailed information about the wallet, including balance and withdrawal IBAN.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Wallet information retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WalletInfoResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            )
    })
    @GetMapping("/wallets/info")
    public ResponseEntity<WalletInfoResponseDto> getWalletInfo() {
        WalletInfoResponseDto walletInfoResponseDto = walletService.getWalletInfo();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(walletInfoResponseDto);
    }

    @Operation(
            summary = "Get wallet balance",
            description = """
                    Retrieves the current balance of the wallet.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Wallet balance retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WalletBalanceResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            )
    })
    @GetMapping("/wallets/balance")
    public ResponseEntity<WalletBalanceResponseDto> getWalletBalance() {
        WalletBalanceResponseDto walletBalanceResponseDto = walletService.getWalletBalance();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(walletBalanceResponseDto);
    }

    @Operation(
            summary = "Set withdrawal IBAN",
            description = """
                    Sets or updates the withdrawal IBAN for the wallet.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Withdrawal IBAN set successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            )
    })
    @PatchMapping("/wallets")
    public ResponseEntity<MessageResponseDto> setWithdrawalIban(@RequestParam String withdrawalIban) {
        walletService.setWithdrawalIban(withdrawalIban);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(WalletMessages.WITHDRAWAL_IBAN_SET_SUCCESSFULLY));
    }

    @Operation(
            summary = "Clear withdrawal IBAN",
            description = """
                    Clears the withdrawal IBAN from the wallet.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Withdrawal IBAN cleared successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            )
    })
    @DeleteMapping("/wallets")
    public ResponseEntity<MessageResponseDto> clearWithdrawalIban() {
        walletService.clearWithdrawalIban();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(WalletMessages.WITHDRAWAL_IBAN_CLEARED_SUCCESSFULLY));
    }

}
