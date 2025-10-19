package com.example.schoolmoney.payment;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.PaymentMessages;
import com.example.schoolmoney.domain.wallet.WalletService;
import com.example.schoolmoney.domain.walletoperation.WalletOperationRepository;
import com.example.schoolmoney.payment.adapter.PaymentAdapter;
import com.example.schoolmoney.payment.dto.PaymentNotificationDto;
import com.example.schoolmoney.payment.dto.PaymentSessionDto;
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

    public PaymentSessionDto createPaymentSession(PaymentProviderType providerType, long amountInCents) throws IllegalStateException {
        PaymentAdapter adapter = getAdapter(providerType);
        try {
            UUID userId = securityUtils.getCurrentUserId();

            // TODO move to config
            String PAYMENT_SUCCESS_URL = "http://localhost:8090/api/v1/payments/status/success";
            String PAYMENT_FAILED_URL = "http://localhost:8090/api/v1/payments/status/failed";

            return adapter.createPaymentSession(amountInCents, userId, PAYMENT_SUCCESS_URL, PAYMENT_FAILED_URL);
        } catch (Exception e) {
            throw new IllegalStateException(PaymentMessages.SESSION_CREATION_ERROR);
        }
    }

    // TODO activate payment_failed event in payment provider dashboard and add logic to handle failed payments (wallet, history, email)
    public void handleWebhook(PaymentProviderType providerType, String payload, String sigHeader) {
        PaymentAdapter adapter = getAdapter(providerType);

        PaymentNotificationDto paymentNotificationDto;
        try {
            paymentNotificationDto = adapter.processWebhook(payload, sigHeader);
        } catch (Exception e) {
            throw new IllegalArgumentException(PaymentMessages.WEBHOOK_PROCESSING_ERROR);
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
                .orElseThrow(() -> new IllegalArgumentException(PaymentMessages.UNSUPPORTED_PAYMENT_PROVIDER));
    }

}
