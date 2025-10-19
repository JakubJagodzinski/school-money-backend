package com.example.schoolmoney.payment.adapter.stripe;

import com.example.schoolmoney.common.constants.messages.PaymentMessages;
import com.example.schoolmoney.config.StripeConfig;
import com.example.schoolmoney.payment.PaymentProviderType;
import com.example.schoolmoney.payment.adapter.PaymentAdapter;
import com.example.schoolmoney.payment.dto.PaymentNotificationDto;
import com.example.schoolmoney.payment.dto.PaymentSessionDto;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class StripeAdapter implements PaymentAdapter {

    private final StripeConfig stripeConfig;

    @PostConstruct
    void init() {
        Stripe.apiKey = stripeConfig.getApiKey();
    }

    @Override
    public PaymentProviderType getProviderType() {
        return PaymentProviderType.STRIPE;
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

    @Override
    public PaymentNotificationDto processWebhook(String payload, String signatureHeader) throws SignatureVerificationException {
        Event event = Webhook.constructEvent(payload, signatureHeader, stripeConfig.getWebhookSecret());
        Session session = (Session) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new IllegalStateException(PaymentMessages.SESSION_IS_NULL));

        return PaymentNotificationDto.builder()
                .externalPaymentId(session.getId())
                .eventType(event.getType())
                .userId(UUID.fromString(session.getMetadata().get("userId")))
                .amountInCents(session.getAmountTotal())
                .currency(session.getCurrency())
                .rawEvent(payload)
                .build();
    }

}
