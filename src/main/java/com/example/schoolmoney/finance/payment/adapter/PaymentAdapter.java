package com.example.schoolmoney.finance.payment.adapter;

import com.example.schoolmoney.finance.payment.ProviderType;
import com.example.schoolmoney.finance.payment.dto.PaymentSessionDto;

import java.util.UUID;

public interface PaymentAdapter {

    ProviderType getProviderType();

    PaymentSessionDto createPaymentSession(long amountInCents, UUID userId, String successUrl, String cancelUrl) throws Exception;

}
