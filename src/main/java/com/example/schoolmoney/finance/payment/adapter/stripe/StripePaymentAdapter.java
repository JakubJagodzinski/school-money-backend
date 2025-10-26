package com.example.schoolmoney.finance.payment.adapter.stripe;

import com.example.schoolmoney.finance.payment.ProviderType;
import com.example.schoolmoney.finance.payment.adapter.PaymentAdapter;
import com.example.schoolmoney.finance.payment.dto.PaymentSessionDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
    public PaymentSessionDto createPaymentSession(long amountInCents, UUID userId, String successUrl, String cancelUrl) throws StripeException {
        String PAYMENT_NAME = "Wallet Top-Up";

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("pln")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(PAYMENT_NAME)
                                                                .build()
                                                )
                                                .build())
                                .build()
                )
                .putMetadata("userId", userId.toString())
                .build();

        Session session = Session.create(params);

        return PaymentSessionDto.builder()
                .sessionId(session.getId())
                .checkoutUrl(session.getUrl())
                .build();
    }

}
