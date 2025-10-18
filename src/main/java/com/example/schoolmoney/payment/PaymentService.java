package com.example.schoolmoney.payment;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.PaymentMessages;
import com.example.schoolmoney.domain.walletoperation.WalletOperationService;
import com.example.schoolmoney.payment.dto.PaymentNotificationDto;
import com.example.schoolmoney.payment.dto.PaymentSessionDto;
import jakarta.persistence.EntityNotFoundException;
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

    private final WalletOperationService walletOperationService;

    private final SecurityUtils securityUtils;

    // TODO change exception type
    public PaymentSessionDto createPaymentSession(PaymentProviderType providerType, long amountInCents) throws EntityNotFoundException {
        try {
            PaymentAdapter adapter = getAdapter(providerType);

            UUID userId = securityUtils.getCurrentUserId();

            if (userId == null) {
                log.error(PaymentMessages.USER_NOT_AUTHENTICATED);
                throw new EntityNotFoundException(PaymentMessages.USER_NOT_AUTHENTICATED);
            }

            String PAYMENT_SUCCESS_URL = "http://localhost:8090/api/v1/payments/success";
            String PAYMENT_FAILED_URL = "http://localhost:8090/api/v1/payments/failed";
            return adapter.createPaymentSession(amountInCents, userId, PAYMENT_SUCCESS_URL, PAYMENT_FAILED_URL);
        } catch (Exception e) {
            throw new EntityNotFoundException(PaymentMessages.SESSION_CREATION_ERROR);
        }
    }

    public void handleWebhook(PaymentProviderType providerType, String payload, String sigHeader) throws Exception {
        PaymentAdapter adapter = getAdapter(providerType);

        PaymentNotificationDto paymentNotificationDto = adapter.processWebhook(payload, sigHeader);

        walletOperationService.registerWalletDepositOperation(
                paymentNotificationDto.getUserId(),
                paymentNotificationDto.getAmountInCents(),
                paymentNotificationDto.getExternalPaymentId(),
                adapter.getProviderType()
        );
    }

    private PaymentAdapter getAdapter(PaymentProviderType providerType) {
        return paymentAdapters.stream()
                .filter(a -> a.getProviderType().equals(providerType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(PaymentMessages.UNSUPPORTED_PAYMENT_PROVIDER));
    }

}
