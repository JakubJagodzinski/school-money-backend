package com.example.schoolmoney.domain.wallet;

import com.example.schoolmoney.common.constants.messages.WalletMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.domain.wallet.dto.response.WalletBalanceResponseDto;
import com.example.schoolmoney.domain.wallet.dto.response.WalletInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/wallets/info")
    public ResponseEntity<WalletInfoResponseDto> getWalletInfo() {
        WalletInfoResponseDto walletInfoResponseDto = walletService.getWalletInfo();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(walletInfoResponseDto);
    }

    @GetMapping("/wallets/balance")
    public ResponseEntity<WalletBalanceResponseDto> getWalletBalance() {
        WalletBalanceResponseDto walletBalanceResponseDto = walletService.getWalletBalance();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(walletBalanceResponseDto);
    }

    @PatchMapping("/wallets")
    public ResponseEntity<MessageResponseDto> setWithdrawalIban(@RequestParam String withdrawalIban) {
        walletService.setWithdrawalIban(withdrawalIban);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(WalletMessages.WITHDRAWAL_IBAN_SET_SUCCESSFULLY));
    }

    @DeleteMapping("/wallets")
    public ResponseEntity<MessageResponseDto> clearWithdrawalIban() {
        walletService.clearWithdrawalIban();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(WalletMessages.WITHDRAWAL_IBAN_CLEARED_SUCCESSFULLY));
    }

}
