package com.example.schoolmoney.domain.walletoperation;

import com.example.schoolmoney.common.constants.messages.ParentMessages;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.wallet.Wallet;
import com.example.schoolmoney.domain.wallet.WalletRepository;
import com.example.schoolmoney.payment.PaymentProviderType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class WalletOperationService {

    private final WalletOperationRepository walletOperationRepository;

    private final WalletRepository walletRepository;
    private final ParentRepository parentRepository;

    @Transactional
    public void registerWalletDepositOperation(UUID userId, long amountInCents, String externalPaymentId, PaymentProviderType paymentProviderType) throws EntityNotFoundException, IllegalStateException {
        log.debug("enter registerExternalPayment for userId: {}, amountInCents: {}, externalPaymentId: {}", userId, amountInCents, externalPaymentId);

        if (!parentRepository.existsById(userId)) {
            log.error(ParentMessages.PARENT_NOT_FOUND);
            throw new IllegalStateException(ParentMessages.PARENT_NOT_FOUND);
        }

        if (walletOperationRepository.existsByExternalPaymentIdAndPaymentProviderType(externalPaymentId, paymentProviderType)) {
            log.warn("wallet operation already exists for externalPaymentId: {}, paymentProviderType: {}", externalPaymentId, paymentProviderType);
            return; // webhook idempotency check
        }

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        wallet.setBalanceInCents(wallet.getBalanceInCents() + amountInCents);
        walletRepository.save(wallet);
        log.info("wallet updated {}", wallet);

        WalletOperation walletPaymentOperation = WalletOperation.builder()
                .wallet(wallet)
                .externalPaymentId(externalPaymentId)
                .paymentProviderType(paymentProviderType)
                .amountInCents(amountInCents)
                .operationType(WalletOperationType.DEPOSIT)
                .operationStatus(WalletOperationStatus.SUCCESS)
                .build();

        walletOperationRepository.save(walletPaymentOperation);
        log.info("wallet operation saved {}", walletPaymentOperation);

        log.debug("exit registerExternalPayment");
    }

}
