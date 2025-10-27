package com.example.schoolmoney.finance.payment;

import com.example.schoolmoney.common.constants.messages.PaymentMessages;
import com.example.schoolmoney.finance.ProviderType;
import com.example.schoolmoney.finance.payment.adapter.PaymentAdapter;
import com.example.schoolmoney.finance.payment.dto.PaymentRequestDto;
import com.example.schoolmoney.finance.payment.dto.PaymentSessionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final List<PaymentAdapter> paymentAdapters;

    public PaymentSessionDto createPaymentSession(PaymentRequestDto paymentRequestDto) throws IllegalStateException {
        PaymentAdapter adapter = getAdapter(paymentRequestDto.getProviderType());

        try {
            return adapter.createPaymentSession(paymentRequestDto);
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
