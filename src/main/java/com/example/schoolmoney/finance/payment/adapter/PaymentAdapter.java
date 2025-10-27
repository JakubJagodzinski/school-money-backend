package com.example.schoolmoney.finance.payment.adapter;

import com.example.schoolmoney.finance.ProviderType;
import com.example.schoolmoney.finance.payment.dto.PaymentRequestDto;
import com.example.schoolmoney.finance.payment.dto.PaymentSessionDto;

public interface PaymentAdapter {

    ProviderType getProviderType();

    PaymentSessionDto createPaymentSession(PaymentRequestDto paymentRequestDto) throws Exception;

}
