package com.example.schoolmoney.domain.financialoperation;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.domain.wallet.Wallet;
import com.example.schoolmoney.domain.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FinancialOperationService {

    private final FinancialOperationRepository financialOperationRepository;

    private final WalletRepository walletRepository;

    private final SecurityUtils securityUtils;

    public Page<FinancialOperationView> getUserFinancialOperationHistory(Pageable pageable) {
        log.debug("Enter getUserFinancialOperationHistory(pageable={})", pageable);

        UUID userId = securityUtils.getCurrentUserId();

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        Page<FinancialOperationView> userFinancialOperationPage = financialOperationRepository.findFinancialOperations(userId, wallet.getWalletId(), pageable);

        log.debug("Exit getUserFinancialOperationHistory");
        return userFinancialOperationPage;
    }

}
