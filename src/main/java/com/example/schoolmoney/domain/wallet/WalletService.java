package com.example.schoolmoney.domain.wallet;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.EmailMessages;
import com.example.schoolmoney.common.constants.messages.domain.ParentMessages;
import com.example.schoolmoney.common.constants.messages.domain.WalletMessages;
import com.example.schoolmoney.common.constants.messages.domain.WalletOperationMessages;
import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.wallet.dto.WalletMapper;
import com.example.schoolmoney.domain.wallet.dto.response.WalletBalanceResponseDto;
import com.example.schoolmoney.domain.wallet.dto.response.WalletInfoResponseDto;
import com.example.schoolmoney.domain.walletoperation.WalletOperation;
import com.example.schoolmoney.domain.walletoperation.WalletOperationRepository;
import com.example.schoolmoney.domain.walletoperation.WalletOperationType;
import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.payment.PaymentProviderType;
import com.example.schoolmoney.utils.IbanMasker;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
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

    private final WalletOperationRepository walletOperationRepository;

    private final SecurityUtils securityUtils;

    private final EmailService emailService;

    @Transactional
    public void createWallet(UUID parentId) throws EntityNotFoundException, EntityExistsException {
        log.debug("Enter createWallet(parentId={})", parentId);

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> {
                    log.warn(ParentMessages.PARENT_NOT_FOUND);
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
        log.info("Wallet saved {}", wallet);

        log.debug("Exit createWallet");
    }

    public WalletInfoResponseDto getWalletInfo() {
        log.debug("Enter getWalletInfo");

        UUID userId = securityUtils.getCurrentUserId();

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        log.debug("Exit getWalletInfo");
        return walletMapper.toInfoDto(wallet);
    }

    public WalletBalanceResponseDto getWalletBalance() {
        log.debug("Enter getWalletBalance");

        UUID userId = securityUtils.getCurrentUserId();

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        log.debug("Exit getWalletBalance");
        return walletMapper.toBalanceDto(wallet);
    }

    @Transactional
    public void setWithdrawalIban(String withdrawalIban) {
        log.debug("Enter setWithdrawalIban(withdrawalIban={})", withdrawalIban);

        UUID userId = securityUtils.getCurrentUserId();

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        wallet.setWithdrawalIban(withdrawalIban);
        walletRepository.save(wallet);
        log.info("Wallet saved {}", wallet);

        log.debug("Exit setWithdrawalIban");
    }

    @Transactional
    public void clearWithdrawalIban() {
        log.debug("Enter clearWithdrawalIban");

        UUID userId = securityUtils.getCurrentUserId();

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        wallet.setWithdrawalIban(null);
        walletRepository.save(wallet);
        log.info("Wallet saved {}", wallet);

        log.debug("Exit clearWithdrawalIban");
    }

    @Transactional
    public void registerWalletTopUp(UUID userId, long amountInCents, String externalPaymentId, PaymentProviderType paymentProviderType) throws EntityNotFoundException, MailSendException {
        log.debug("Enter registerWalletTopUp(userId={}, amountInCents={}, externalPaymentId={})", userId, amountInCents, externalPaymentId);

        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn(ParentMessages.PARENT_NOT_FOUND);
                    return new EntityNotFoundException(ParentMessages.PARENT_NOT_FOUND);
                });

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        wallet.setBalanceInCents(wallet.getBalanceInCents() + amountInCents);
        walletRepository.save(wallet);
        log.info("wallet updated {}", wallet);

        WalletOperation walletPaymentOperation = WalletOperation.builder()
                .wallet(wallet)
                .externalPaymentId(externalPaymentId)
                .paymentProviderType(paymentProviderType)
                .amountInCents(amountInCents)
                .operationType(WalletOperationType.WALLET_TOP_UP)
                .operationStatus(FinancialOperationStatus.SUCCESS)
                .build();

        walletOperationRepository.save(walletPaymentOperation);
        log.info("Wallet operation saved {}", walletPaymentOperation);

        try {
            emailService.sendWalletTopUpEmail(parent.getEmail(), parent.getFirstName(), amountInCents);
            log.debug("Exit registerWalletTopUp");
        } catch (MessagingException e) {
            log.error(EmailMessages.FAILED_TO_SEND_WALLET_TOP_UP_EMAIL, e);
            throw new MailSendException(EmailMessages.FAILED_TO_SEND_WALLET_TOP_UP_EMAIL, e);
        }
    }

    @Transactional
    public void withdrawFunds(long withdrawalAmountInCents) throws IllegalStateException, IllegalArgumentException {
        log.debug("Enter withdrawFunds(amountInCents={})", withdrawalAmountInCents);

        UUID userId = securityUtils.getCurrentUserId();

        Parent parent = parentRepository.getReferenceById(userId);

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        if (wallet.getWithdrawalIban() == null) {
            log.warn(WalletMessages.WITHDRAWAL_IBAN_NOT_SET);
            throw new IllegalStateException(WalletMessages.WITHDRAWAL_IBAN_NOT_SET);
        }

        if (withdrawalAmountInCents <= 0) {
            log.warn(WalletOperationMessages.WITHDRAWAL_AMOUNT_MUST_BE_GREATER_THAN_ZERO);
            throw new IllegalArgumentException(WalletOperationMessages.WITHDRAWAL_AMOUNT_MUST_BE_GREATER_THAN_ZERO);
        }

        if (wallet.getBalanceInCents() < withdrawalAmountInCents) {
            log.warn(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
            throw new IllegalStateException(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
        }

        // TODO add stripe withdrawal
        UUID externalPaymentId = UUID.randomUUID();

        wallet.setBalanceInCents(wallet.getBalanceInCents() - withdrawalAmountInCents);
        walletRepository.save(wallet);
        log.info("Wallet updated {}", wallet);

        WalletOperation walletWithdrawalOperation = WalletOperation.builder()
                .wallet(wallet)
                .externalPaymentId(externalPaymentId.toString())
                .paymentProviderType(PaymentProviderType.STRIPE)
                .iban(IbanMasker.maskIban(wallet.getWithdrawalIban()))
                .amountInCents(withdrawalAmountInCents)
                .operationType(WalletOperationType.WALLET_WITHDRAWAL)
                .operationStatus(FinancialOperationStatus.SUCCESS)
                .build();

        walletOperationRepository.save(walletWithdrawalOperation);
        log.info("Wallet operation saved {}", walletWithdrawalOperation);

        try {
            emailService.sendWalletWithdrawalEmail(parent.getEmail(), parent.getFirstName(), withdrawalAmountInCents);
            log.debug("Exit withdrawFunds");
        } catch (Exception e) {
            log.error(EmailMessages.FAILED_TO_SEND_WALLET_WITHDRAWAL_EMAIL);
        }
    }

}
