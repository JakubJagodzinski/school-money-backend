package com.example.schoolmoney.finance.payout.adapter.stripe;

import com.example.schoolmoney.common.constants.messages.PayoutMessages;
import com.example.schoolmoney.domain.wallet.WalletService;
import com.example.schoolmoney.finance.ProviderType;
import com.example.schoolmoney.finance.payout.dto.PayoutNotificationDto;
import com.stripe.model.Event;
import com.stripe.model.Payout;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class StripePayoutWebhookService {

    private final StripePayoutConfig stripePayoutConfig;

    private final WalletService walletService;

    public void handlePayoutWebhook(String payload, String signatureHeader) throws IllegalStateException {
        try {
            Event event = Webhook.constructEvent(payload, signatureHeader, stripePayoutConfig.getPayoutWebhookSecret());

            Payout payout = (Payout) event.getDataObjectDeserializer().getObject()
                    .orElseThrow(() -> {
                        log.warn(PayoutMessages.PAYOUT_OBJECT_IS_NULL);
                        return new IllegalStateException(PayoutMessages.PAYOUT_OBJECT_IS_NULL);
                    });

            String userIdStr = payout.getMetadata().get("userId");
            UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;

            String operationIdStr = payout.getMetadata().get("operationId");
            UUID operationId = operationIdStr != null ? UUID.fromString(operationIdStr) : null;

            PayoutNotificationDto payoutNotificationDto = PayoutNotificationDto.builder()
                    .externalPayoutId(payout.getId())
                    .eventType(event.getType())
                    .userId(userId)
                    .operationId(operationId)
                    .amountInCents(payout.getAmount())
                    .currency(Currency.getInstance(payout.getCurrency().toUpperCase()))
                    .rawEvent(payload)
                    .providerType(ProviderType.STRIPE)
                    .build();

            walletService.finalizeWalletWithdrawal(payoutNotificationDto);
        } catch (Exception e) {
            log.error(PayoutMessages.PAYOUT_WEBHOOK_PROCESSING_ERROR, e);
            throw new IllegalStateException(PayoutMessages.PAYOUT_WEBHOOK_PROCESSING_ERROR);
        }
    }

}
