package com.example.schoolmoney.domain.wallet;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.ParentMessages;
import com.example.schoolmoney.common.constants.messages.domain.WalletMessages;
import com.example.schoolmoney.common.constants.messages.domain.WalletOperationMessages;
import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentFinder;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.wallet.dto.WalletMapper;
import com.example.schoolmoney.domain.wallet.dto.request.InitializeWalletTopUpRequestDto;
import com.example.schoolmoney.domain.wallet.dto.request.PerformWalletWithdrawalRequestDto;
import com.example.schoolmoney.domain.wallet.dto.response.WalletBalanceResponseDto;
import com.example.schoolmoney.domain.wallet.dto.response.WalletInfoResponseDto;
import com.example.schoolmoney.domain.walletoperation.WalletOperation;
import com.example.schoolmoney.domain.walletoperation.WalletOperationRepository;
import com.example.schoolmoney.domain.walletoperation.WalletOperationType;
import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.finance.FinanceConfiguration;
import com.example.schoolmoney.finance.ProviderType;
import com.example.schoolmoney.finance.payment.PaymentProperties;
import com.example.schoolmoney.finance.payment.PaymentService;
import com.example.schoolmoney.finance.payment.dto.PaymentNotificationDto;
import com.example.schoolmoney.finance.payment.dto.PaymentRequestDto;
import com.example.schoolmoney.finance.payment.dto.PaymentSessionDto;
import com.example.schoolmoney.finance.payout.PayoutService;
import com.example.schoolmoney.finance.payout.dto.PayoutNotificationDto;
import com.example.schoolmoney.finance.payout.dto.PayoutRequestDto;
import com.example.schoolmoney.properties.ServerProperties;
import com.example.schoolmoney.utils.IbanUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class WalletService {

    private final ProviderType withdrawalProviderType = ProviderType.STRIPE;

    private final WalletMapper walletMapper;

    private final WalletRepository walletRepository;

    private final ParentRepository parentRepository;

    private final WalletOperationRepository walletOperationRepository;

    private final PaymentService paymentService;

    private final EmailService emailService;

    private final SecurityUtils securityUtils;

    private final PayoutService payoutService;

    private final FinanceConfiguration financeConfiguration;

    private final ServerProperties serverProperties;

    private final PaymentProperties paymentProperties;

    private final ParentFinder parentFinder;

    @Transactional
    public void createWallet(UUID parentId) {
        createWallet(parentId, null);
    }

    @Transactional
    public void createWallet(UUID parentId, Currency currency) throws EntityNotFoundException, EntityExistsException {
        log.debug("Enter createWallet(parentId={}, currency={})", parentId, currency);

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> {
                    log.warn(ParentMessages.PARENT_NOT_FOUND);
                    return new EntityNotFoundException(ParentMessages.PARENT_NOT_FOUND);
                });

        if (walletRepository.existsByParent_UserId(parentId)) {
            log.warn(WalletMessages.WALLET_ALREADY_EXISTS);
            throw new EntityExistsException(WalletMessages.WALLET_ALREADY_EXISTS);
        }

        if (currency == null) {
            currency = financeConfiguration.getCurrency();
        }

        Wallet wallet = Wallet
                .builder()
                .parent(parent)
                .currency(currency)
                .balanceInCents(financeConfiguration.getStartingBalance())
                .build();

        walletRepository.save(wallet);
        log.info("Wallet with id={} saved successfully", wallet.getWalletId());

        if (financeConfiguration.getStartingBalance() > 0) {
            WalletOperation walletOperation = WalletOperation.builder()
                    .wallet(wallet)
                    .amountInCents(financeConfiguration.getStartingBalance())
                    .currency(currency)
                    .operationType(WalletOperationType.WELCOME_BONUS)
                    .operationStatus(FinancialOperationStatus.SUCCESS)
                    .providerType(ProviderType.INTERNAL)
                    .processedAt(Instant.now())
                    .build();

            walletOperationRepository.save(walletOperation);
            log.info("Wallet operation saved with id={}", walletOperation.getWalletOperationId());
        }

        log.debug("Exit createWallet(parentId={}, currency={})", parentId, currency);
    }

    @Transactional(readOnly = true)
    public WalletInfoResponseDto getWalletInfo() {
        log.debug("Enter getWalletInfo()");

        UUID userId = securityUtils.getCurrentUserId();
        parentFinder.assertParentExists(userId);

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        log.debug("Exit getWalletInfo()");
        return walletMapper.toInfoDto(wallet);
    }

    @Transactional(readOnly = true)
    public WalletBalanceResponseDto getWalletBalance() {
        log.debug("Enter getWalletBalance()");

        UUID userId = securityUtils.getCurrentUserId();
        parentFinder.assertParentExists(userId);

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        log.debug("Exit getWalletBalance()");
        return walletMapper.toBalanceDto(wallet);
    }

    @Transactional
    public void setWithdrawalIban(String withdrawalIban) {
        log.debug("Enter setWithdrawalIban()");

        UUID userId = securityUtils.getCurrentUserId();
        parentFinder.assertParentExists(userId);

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        wallet.setWithdrawalIban(withdrawalIban);
        walletRepository.save(wallet);
        log.info("Wallet with id={} saved successfully", wallet.getWalletId());

        log.debug("Exit setWithdrawalIban()");
    }

    @Transactional
    public void clearWithdrawalIban() {
        log.debug("Enter clearWithdrawalIban()");

        UUID userId = securityUtils.getCurrentUserId();
        parentFinder.assertParentExists(userId);

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        wallet.setWithdrawalIban(null);
        walletRepository.save(wallet);
        log.info("Wallet with id={} saved successfully", wallet.getWalletId());

        log.debug("Exit clearWithdrawalIban()");
    }

    @Transactional
    public PaymentSessionDto initializeWalletTopUp(InitializeWalletTopUpRequestDto requestDto) {
        log.debug("Enter initializeWalletTopUp()");

        UUID userId = securityUtils.getCurrentUserId();
        parentFinder.assertParentExists(userId);

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        WalletOperation walletOperation = WalletOperation.builder()
                .wallet(wallet)
                .amountInCents(requestDto.getAmountInCents())
                .currency(financeConfiguration.getCurrency())
                .operationType(WalletOperationType.WALLET_TOP_UP)
                .operationStatus(FinancialOperationStatus.PENDING)
                .providerType(requestDto.getProviderType())
                .build();

        WalletOperation pendingOperation = walletOperationRepository.save(walletOperation);
        log.info("Wallet operation saved with id={}", walletOperation.getWalletOperationId());

        String successRedirectUrl = requestDto.getSuccessRedirectUrl() != null ?
                requestDto.getSuccessRedirectUrl() :
                serverProperties.getPublicAddress() + paymentProperties.getSuccessPath();
        String cancelRedirectUrl = requestDto.getCancelRedirectUrl() != null ?
                requestDto.getCancelRedirectUrl() :
                serverProperties.getPublicAddress() + paymentProperties.getFailedPath();

        PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
                .providerType(requestDto.getProviderType())
                .userId(userId)
                .paymentName("Wallet Top-Up")
                .operationId(walletOperation.getWalletOperationId())
                .amountInCents(requestDto.getAmountInCents())
                .currency(financeConfiguration.getCurrency())
                .successUrl(successRedirectUrl)
                .cancelUrl(cancelRedirectUrl)
                .build();

        PaymentSessionDto paymentSessionDto = paymentService.createPaymentSession(paymentRequestDto);

        pendingOperation.setExternalOperationId(paymentSessionDto.getSessionId());
        walletOperationRepository.save(pendingOperation);

        log.debug("Exit initializeWalletTopUp()");
        return paymentSessionDto;
    }

    @Transactional
    public void finalizeWalletTopUp(PaymentNotificationDto paymentNotificationDto) throws EntityNotFoundException {
        log.debug("Enter registerWalletTopUp()");

        WalletOperation walletOperation = walletOperationRepository.findById(paymentNotificationDto.getOperationId())
                .orElseThrow(() -> {
                    log.warn(WalletOperationMessages.WALLET_OPERATION_NOT_FOUND);
                    return new EntityNotFoundException(WalletOperationMessages.WALLET_OPERATION_NOT_FOUND);
                });

        Parent parent = walletOperation.getWallet().getParent();

        Wallet wallet = walletOperation.getWallet();

        switch (paymentNotificationDto.getEventType()) {
            case "checkout.session.completed":
                log.info("Payment successful");

                wallet.increaseBalanceInCents(walletOperation.getAmountInCents());
                log.info("Wallet balance increased");

                walletOperation.setProcessedAt(Instant.now());
                walletOperation.setOperationStatus(FinancialOperationStatus.SUCCESS);

                emailService.sendWalletTopUpEmail(
                        parent.getEmail(),
                        parent.getFirstName(),
                        walletOperation.getAmountInCents(),
                        walletOperation.getCurrency(),
                        parent.isNotificationsEnabled()
                );
                break;
            case "payment_intent.payment_failed":
                log.warn("Payment failed");

                walletOperation.setProcessedAt(Instant.now());
                walletOperation.setOperationStatus(FinancialOperationStatus.FAILED);
                break;
            case "checkout.session.expired":
                log.warn("Session expired");

                walletOperation.setProcessedAt(Instant.now());
                walletOperation.setOperationStatus(FinancialOperationStatus.EXPIRED);
                break;
            default:
                log.warn("Unhandled payment event type: {}", paymentNotificationDto.getEventType());
        }

        walletOperationRepository.save(walletOperation);
        walletRepository.save(wallet);
        log.info("Wallet operation saved with id={}", walletOperation.getWalletOperationId());

        log.debug("Exit registerWalletTopUp()");
    }

    private void validateWalletWithdrawal(Wallet wallet, long amountInCents) throws IllegalStateException, IllegalArgumentException {
        if (wallet.getWithdrawalIban() == null) {
            log.warn(WalletMessages.WITHDRAWAL_IBAN_NOT_SET);
            throw new IllegalStateException(WalletMessages.WITHDRAWAL_IBAN_NOT_SET);
        }

        if (amountInCents <= 0) {
            log.warn(WalletOperationMessages.WITHDRAWAL_AMOUNT_MUST_BE_GREATER_THAN_ZERO);
            throw new IllegalArgumentException(WalletOperationMessages.WITHDRAWAL_AMOUNT_MUST_BE_GREATER_THAN_ZERO);
        }

        if (wallet.getAvailableBalanceInCents() < amountInCents) {
            log.warn(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
            throw new IllegalStateException(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
        }
    }

    @Transactional
    public void initializeWalletWithdrawal(PerformWalletWithdrawalRequestDto requestDto) {
        log.debug("Enter initializeWalletWithdrawal(requestDto={})", requestDto);

        UUID userId = securityUtils.getCurrentUserId();
        parentFinder.assertParentExists(userId);

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        long amountInCents = requestDto.getAmountInCents();

        validateWalletWithdrawal(wallet, amountInCents);

        wallet.increaseReservedBalanceInCents(amountInCents);
        walletRepository.save(wallet);
        log.info("Reserved balance updated for wallet with id={}", wallet.getWalletId());

        WalletOperation walletOperation = WalletOperation.builder()
                .wallet(wallet)
                .amountInCents(amountInCents)
                .currency(financeConfiguration.getCurrency())
                .operationType(WalletOperationType.WALLET_WITHDRAWAL)
                .operationStatus(FinancialOperationStatus.PENDING)
                .providerType(withdrawalProviderType)
                .iban(IbanUtil.maskIban(wallet.getWithdrawalIban()))
                .build();

        WalletOperation pendingOperation = walletOperationRepository.save(walletOperation);
        log.info("Wallet operation saved with id={}", walletOperation.getWalletOperationId());

        PayoutRequestDto payoutRequestDto = buildPayoutRequest(userId, pendingOperation, wallet.getWithdrawalIban());
        String payoutId = payoutService.createPayout(payoutRequestDto);

        pendingOperation.setExternalOperationId(payoutId);
        walletOperationRepository.save(pendingOperation);

        log.debug("Enter initializeWalletWithdrawal(requestDto={})", requestDto);
    }

    private PayoutRequestDto buildPayoutRequest(UUID userId, WalletOperation pendingOperation, String iban) {
        return PayoutRequestDto.builder()
                .providerType(withdrawalProviderType)
                .payoutName("Wallet Withdrawal")
                .operationId(pendingOperation.getWalletOperationId())
                .userId(userId)
                .amountInCents(pendingOperation.getAmountInCents())
                .currency(pendingOperation.getCurrency())
                .iban(iban)
                .build();
    }

    @Transactional
    public void finalizeWalletWithdrawal(PayoutNotificationDto payoutNotificationDto) {
        log.debug("Enter finalizeWalletWithdrawal()");

        WalletOperation walletOperation = walletOperationRepository.findById(payoutNotificationDto.getOperationId())
                .orElseThrow(() -> {
                    log.warn(WalletOperationMessages.WALLET_OPERATION_NOT_FOUND);
                    return new EntityNotFoundException(WalletOperationMessages.WALLET_OPERATION_NOT_FOUND);
                });

        Parent parent = walletOperation.getWallet().getParent();

        Wallet wallet = walletOperation.getWallet();

        switch (payoutNotificationDto.getEventType()) {
            case "payout.paid":
                wallet.decreaseReservedBalanceInCents(walletOperation.getAmountInCents());

                walletOperation.setProcessedAt(Instant.now());
                walletOperation.setOperationStatus(FinancialOperationStatus.SUCCESS);

                emailService.sendWalletWithdrawalEmail(
                        parent.getEmail(),
                        parent.getFirstName(),
                        walletOperation.getAmountInCents(),
                        walletOperation.getCurrency(),
                        parent.isNotificationsEnabled()
                );
                break;
            case "payout.failed":
                wallet.decreaseReservedBalanceInCents(walletOperation.getAmountInCents());

                walletOperation.setProcessedAt(Instant.now());
                walletOperation.setOperationStatus(FinancialOperationStatus.FAILED);
                break;
            default:
                log.warn("Unhandled payout event type: {}", payoutNotificationDto.getEventType());
        }

        walletRepository.save(wallet);
        walletOperationRepository.save(walletOperation);
        log.info("Wallet operation saved with id={}", walletOperation.getWalletOperationId());

        log.debug("Exit finalizeWalletWithdrawal()");
    }

    @Transactional
    public void performWalletWithdrawal(PerformWalletWithdrawalRequestDto requestDto) {
        log.debug("Enter performWalletWithdrawal(requestDto={})", requestDto);

        UUID userId = securityUtils.getCurrentUserId();
        parentFinder.assertParentExists(userId);

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        validateWalletWithdrawal(wallet, requestDto.getAmountInCents());

        wallet.decreaseBalanceInCents(requestDto.getAmountInCents());
        walletRepository.save(wallet);
        log.info("Wallet balance updated for wallet with id={}", wallet.getWalletId());

        WalletOperation walletOperation = WalletOperation.builder()
                .wallet(wallet)
                .amountInCents(requestDto.getAmountInCents())
                .currency(financeConfiguration.getCurrency())
                .operationType(WalletOperationType.WALLET_WITHDRAWAL)
                .operationStatus(FinancialOperationStatus.SUCCESS)
                .providerType(ProviderType.INTERNAL)
                .iban(IbanUtil.maskIban(wallet.getWithdrawalIban()))
                .processedAt(Instant.now())
                .build();

        walletOperationRepository.save(walletOperation);
        log.info("Wallet operation saved with id={}", walletOperation.getWalletOperationId());

        log.debug("Exit performWalletWithdrawal(requestDto={})", requestDto);
    }

}
