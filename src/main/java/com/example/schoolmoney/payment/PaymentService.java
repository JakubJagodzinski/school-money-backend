package com.example.schoolmoney.payment;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.PaymentMessages;
import com.example.schoolmoney.domain.wallet.WalletService;
import com.example.schoolmoney.domain.walletoperation.WalletOperationRepository;
import com.example.schoolmoney.payment.adapter.PaymentAdapter;
import com.example.schoolmoney.payment.dto.PaymentNotificationDto;
import com.example.schoolmoney.payment.dto.PaymentSessionDto;
import com.example.schoolmoney.properties.ServerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final List<PaymentAdapter> paymentAdapters;

    private final SecurityUtils securityUtils;

    private final WalletService walletService;

    private final WalletOperationRepository walletOperationRepository;

    private final ServerProperties serverProperties;

    private final PaymentProperties paymentProperties;

    public PaymentSessionDto createPaymentSession(PaymentProviderType providerType, long amountInCents) throws IllegalStateException {
        PaymentAdapter adapter = getAdapter(providerType);
        try {
            UUID userId = securityUtils.getCurrentUserId();

            String successUrl = serverProperties.getPublicAddress() + paymentProperties.getSuccessPath();
            String failedUrl = serverProperties.getPublicAddress() + paymentProperties.getFailedPath();
            return adapter.createPaymentSession(amountInCents, userId, successUrl, failedUrl);
        } catch (Exception e) {
            throw new IllegalStateException(PaymentMessages.SESSION_CREATION_ERROR);
        }
    }

    // TODO activate payment_failed event in payment provider dashboard and add logic to handle failed payments (wallet, history, email)
    public void handleWebhook(PaymentProviderType providerType, String payload, String sigHeader) throws IllegalStateException {
        PaymentAdapter adapter = getAdapter(providerType);

        PaymentNotificationDto paymentNotificationDto;
        try {
            paymentNotificationDto = adapter.processWebhook(payload, sigHeader);
        } catch (Exception e) {
            log.error(PaymentMessages.WEBHOOK_PROCESSING_ERROR, e);
            throw new IllegalStateException(PaymentMessages.WEBHOOK_PROCESSING_ERROR);
        }

        String externalPaymentId = paymentNotificationDto.getExternalPaymentId();
        UUID userId = paymentNotificationDto.getUserId();
        long amountInCents = paymentNotificationDto.getAmountInCents();
        PaymentProviderType paymentProviderType = adapter.getProviderType();

        if (walletOperationRepository.existsByExternalPaymentIdAndPaymentProviderType(externalPaymentId, paymentProviderType)) {
            log.warn("Wallet operation already exists for externalPaymentId: {}, paymentProviderType: {}", externalPaymentId, paymentProviderType);
            return;
        }

        walletService.registerWalletTopUp(
                userId,
                amountInCents,
                externalPaymentId,
                paymentProviderType
        );
    }

    private PaymentAdapter getAdapter(PaymentProviderType providerType) throws IllegalArgumentException {
        return paymentAdapters.stream()
                .filter(a -> a.getProviderType().equals(providerType))
                .findFirst()
                .orElseThrow(() -> {
                    log.error(PaymentMessages.UNSUPPORTED_PAYMENT_PROVIDER);
                    return new IllegalArgumentException(PaymentMessages.UNSUPPORTED_PAYMENT_PROVIDER);
                });
    }

}
