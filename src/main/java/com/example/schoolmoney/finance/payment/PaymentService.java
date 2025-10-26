package com.example.schoolmoney.finance.payment;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.PaymentMessages;
import com.example.schoolmoney.finance.payment.adapter.PaymentAdapter;
import com.example.schoolmoney.finance.payment.dto.PaymentSessionDto;
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

    private final ServerProperties serverProperties;

    private final PaymentProperties paymentProperties;

    private final SecurityUtils securityUtils;

    public PaymentSessionDto createPaymentSession(ProviderType providerType, long amountInCents) throws IllegalStateException {
        PaymentAdapter adapter = getAdapter(providerType);
        try {
            UUID userId = securityUtils.getCurrentUserId();

            String successUrl = serverProperties.getPublicAddress() + paymentProperties.getSuccessPath();
            String failedUrl = serverProperties.getPublicAddress() + paymentProperties.getFailedPath();
            return adapter.createPaymentSession(amountInCents, userId, successUrl, failedUrl);
        } catch (Exception e) {
            throw new IllegalStateException(PaymentMessages.PAYMENT_SESSION_CREATION_ERROR);
        }
    }

    private PaymentAdapter getAdapter(ProviderType providerType) throws IllegalArgumentException {
        return paymentAdapters.stream()
                .filter(a -> a.getProviderType().equals(providerType))
                .findFirst()
                .orElseThrow(() -> {
                    log.error(PaymentMessages.UNSUPPORTED_PAYMENT_PROVIDER);
                    return new IllegalArgumentException(PaymentMessages.UNSUPPORTED_PAYMENT_PROVIDER);
                });
    }

}
