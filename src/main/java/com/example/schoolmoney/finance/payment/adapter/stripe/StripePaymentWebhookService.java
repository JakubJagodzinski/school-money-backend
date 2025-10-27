package com.example.schoolmoney.finance.payment.adapter.stripe;

import com.example.schoolmoney.common.constants.messages.PaymentMessages;
import com.example.schoolmoney.domain.wallet.WalletService;
import com.example.schoolmoney.finance.ProviderType;
import com.example.schoolmoney.finance.payment.dto.PaymentNotificationDto;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class StripePaymentWebhookService {

    private final StripePaymentConfig stripePaymentConfig;

    private final WalletService walletService;

    public void handlePaymentWebhook(String payload, String signatureHeader) throws IllegalStateException {
        try {
            Event event = Webhook.constructEvent(payload, signatureHeader, stripePaymentConfig.getPaymentWebhookSecret());
            Session session = (Session) event.getDataObjectDeserializer().getObject()
                    .orElseThrow(() -> {
                        log.warn(PaymentMessages.PAYMENT_SESSION_IS_NULL);
                        return new IllegalStateException(PaymentMessages.PAYMENT_SESSION_IS_NULL);
                    });

            PaymentNotificationDto paymentNotificationDto = PaymentNotificationDto.builder()
                    .externalPaymentId(session.getId())
                    .eventType(event.getType())
                    .userId(UUID.fromString(session.getMetadata().get("userId")))
                    .operationId(UUID.fromString(session.getMetadata().get("operationId")))
                    .amountInCents(session.getAmountTotal())
                    .currency(session.getCurrency())
                    .rawEvent(payload)
                    .providerType(ProviderType.STRIPE)
                    .build();

            walletService.finalizeWalletTopUp(paymentNotificationDto);
        } catch (Exception e) {
            log.error(PaymentMessages.PAYMENT_WEBHOOK_PROCESSING_ERROR, e);
            throw new IllegalStateException(PaymentMessages.PAYMENT_WEBHOOK_PROCESSING_ERROR);
        }
    }

}
