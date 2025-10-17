package com.example.schoolmoney.domain.wallet;

import com.example.schoolmoney.common.constants.messages.WalletMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.domain.wallet.dto.response.WalletResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/wallets")
    public ResponseEntity<WalletResponseDto> getWalletInfo() {
        WalletResponseDto walletResponseDto = walletService.getWalletInfo();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(walletResponseDto);
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
