package com.example.schoolmoney.domain.walletoperation;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.domain.wallet.Wallet;
import com.example.schoolmoney.domain.wallet.WalletRepository;
import com.example.schoolmoney.domain.walletoperation.dto.WalletOperationMapper;
import com.example.schoolmoney.domain.walletoperation.dto.response.WalletOperationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class WalletOperationService {

    private final WalletOperationMapper walletOperationMapper;

    private final WalletOperationRepository walletOperationRepository;

    private final WalletRepository walletRepository;

    private final SecurityUtils securityUtils;

    @Transactional
    public Page<WalletOperationResponseDto> getWalletHistory(Pageable pageable) {
        log.debug("Enter getWalletHistory(pageable={})", pageable);

        UUID userId = securityUtils.getCurrentUserId();

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        Page<WalletOperation> parentWalletOperationPage = walletOperationRepository.findAllByWallet_WalletIdOrderByProcessedAtDesc(wallet.getWalletId(), pageable);

        log.debug("Exit getWalletHistory");
        return parentWalletOperationPage.map(walletOperationMapper::toDto);
    }

}
