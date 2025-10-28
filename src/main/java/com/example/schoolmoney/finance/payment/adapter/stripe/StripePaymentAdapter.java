package com.example.schoolmoney.finance.payment.adapter.stripe;

import com.example.schoolmoney.finance.ProviderType;
import com.example.schoolmoney.finance.payment.adapter.PaymentAdapter;
import com.example.schoolmoney.finance.payment.dto.PaymentRequestDto;
import com.example.schoolmoney.finance.payment.dto.PaymentSessionDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StripePaymentAdapter implements PaymentAdapter {

    private final StripePaymentConfig stripePaymentConfig;

    @PostConstruct
    void init() {
        Stripe.apiKey = stripePaymentConfig.getApiKey();
    }

    @Override
    public ProviderType getProviderType() {
        return ProviderType.STRIPE;
    }

    @Override
    public PaymentSessionDto createPaymentSession(PaymentRequestDto paymentRequestDto) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(paymentRequestDto.getSuccessUrl())
                .setCancelUrl(paymentRequestDto.getCancelUrl())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(paymentRequestDto.getCurrency().toString())
                                                .setUnitAmount(paymentRequestDto.getAmountInCents())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(paymentRequestDto.getPaymentName())
                                                                .build()
                                                )
                                                .build())
                                .build()
                )
                .putMetadata("operationId", paymentRequestDto.getOperationId().toString())
                .putMetadata("userId", paymentRequestDto.getUserId().toString())
                .build();

        Session session = Session.create(params);

        return PaymentSessionDto.builder()
                .sessionId(session.getId())
                .checkoutUrl(session.getUrl())
                .build();
    }

}
