package com.example.schoolmoney.domain.wallet;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.ParentMessages;
import com.example.schoolmoney.common.constants.messages.WalletMessages;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.wallet.dto.WalletMapper;
import com.example.schoolmoney.domain.wallet.dto.response.WalletBalanceResponseDto;
import com.example.schoolmoney.domain.wallet.dto.response.WalletInfoResponseDto;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class WalletService {

    private final WalletMapper walletMapper;

    private final WalletRepository walletRepository;

    private final ParentRepository parentRepository;

    private final SecurityUtils securityUtils;

    @Transactional
    public void createWallet(UUID parentId) throws EntityNotFoundException, EntityExistsException {
        log.debug("enter createWallet for parent with id: {}", parentId);

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> {
                    log.error(ParentMessages.PARENT_NOT_FOUND);
                    return new EntityNotFoundException(ParentMessages.PARENT_NOT_FOUND);
                });

        if (walletRepository.existsByParent_UserId(parentId)) {
            log.warn(WalletMessages.WALLET_ALREADY_EXISTS);
            throw new EntityExistsException(WalletMessages.WALLET_ALREADY_EXISTS);
        }

        Wallet wallet = Wallet
                .builder()
                .parent(parent)
                .build();

        walletRepository.save(wallet);
        log.info("wallet saved {}", wallet);

        log.debug("exit createWallet");
    }

    public WalletInfoResponseDto getWalletInfo() {
        log.debug("enter getWalletInfo");

        UUID userId = securityUtils.getCurrentUserId();

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        log.debug("exit getWalletInfo");

        return walletMapper.toInfoDto(wallet);
    }

    public WalletBalanceResponseDto getWalletBalance() {
        log.debug("enter getWalletBalance");

        UUID userId = securityUtils.getCurrentUserId();

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        log.debug("exit getWalletBalance");

        return walletMapper.toBalanceDto(wallet);
    }

    @Transactional
    public void setWithdrawalIban(String withdrawalIban) {
        log.debug("enter setWithdrawalIban {}", withdrawalIban);

        UUID userId = securityUtils.getCurrentUserId();

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        wallet.setWithdrawalIban(withdrawalIban);
        walletRepository.save(wallet);
        log.info("wallet saved {}", wallet);

        log.debug("exit setWithdrawalIban");
    }

    @Transactional
    public void clearWithdrawalIban() {
        log.debug("enter clearWithdrawalIban");

        UUID userId = securityUtils.getCurrentUserId();

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        wallet.setWithdrawalIban(null);
        walletRepository.save(wallet);
        log.info("wallet saved {}", wallet);

        log.debug("exit clearWithdrawalIban");
    }

}
