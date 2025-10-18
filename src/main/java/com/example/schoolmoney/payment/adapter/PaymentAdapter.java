package com.example.schoolmoney.payment.adapter;

import com.example.schoolmoney.payment.PaymentProviderType;
import com.example.schoolmoney.payment.dto.PaymentNotificationDto;
import com.example.schoolmoney.payment.dto.PaymentSessionDto;

import java.util.UUID;

public interface PaymentAdapter {

    PaymentSessionDto createPaymentSession(long amountInCents, UUID userId, String successUrl, String cancelUrl) throws Exception;

    PaymentNotificationDto processWebhook(String payload, String signatureHeader) throws Exception;

    PaymentProviderType getProviderType();

}
