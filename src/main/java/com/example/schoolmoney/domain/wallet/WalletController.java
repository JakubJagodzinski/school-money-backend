package com.example.schoolmoney.domain.wallet;

import com.example.schoolmoney.auth.access.CheckPermission;
import com.example.schoolmoney.common.constants.messages.domain.WalletMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.domain.wallet.dto.response.WalletBalanceResponseDto;
import com.example.schoolmoney.domain.wallet.dto.response.WalletInfoResponseDto;
import com.example.schoolmoney.finance.payment.ProviderType;
import com.example.schoolmoney.finance.payment.dto.PaymentSessionDto;
import com.example.schoolmoney.user.Permission;
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
    @CheckPermission(Permission.WALLET_INFO_READ)
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
    @CheckPermission(Permission.WALLET_BALANCE_READ)
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
    @CheckPermission(Permission.WALLET_IBAN_SET)
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
    @CheckPermission(Permission.WALLET_IBAN_CLEAR)
    @DeleteMapping("/wallets")
    public ResponseEntity<MessageResponseDto> clearWithdrawalIban() {
        walletService.clearWithdrawalIban();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(WalletMessages.WITHDRAWAL_IBAN_CLEARED_SUCCESSFULLY));
    }

    @Operation(
            summary = "Initialize wallet top up session with selected provider",
            description = """
                    Top up your wallet with given amount via selected payment provider
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment session created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentSessionDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Invalid amount",
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
    @CheckPermission(Permission.WALLET_TOP_UP)
    @PostMapping("/wallets/top-up")
    public ResponseEntity<PaymentSessionDto> initializeWalletTopUp(@RequestParam ProviderType providerType, @RequestParam long amountInCents) {
        PaymentSessionDto paymentSessionDto = walletService.initializeWalletTopUp(providerType, amountInCents);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentSessionDto);
    }

    @Operation(
            summary = "Withdraw funds from the wallet",
            description = """
                    Withdraws funds from the wallet via payment provider to real account with IBAN set in wallet info.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Wallet withdrawal operation processed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Invalid withdrawal amount",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - Insufficient funds available for withdrawal",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            )
    })
    @CheckPermission(Permission.WALLET_WITHDRAW)
    @PostMapping("/wallet/withdraw")
    public ResponseEntity<MessageResponseDto> withdrawFunds(@RequestParam long withdrawalAmountInCents) {
        walletService.withdrawFunds(withdrawalAmountInCents);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(WalletMessages.WITHDRAWAL_OPERATION_PROCESSED_SUCCESSFULLY));
    }

}
