package com.example.schoolmoney.finance.payout.adapter.stripe;

import com.example.schoolmoney.common.constants.messages.PayoutMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class StripePayoutWebhookController {

    private final StripePayoutWebhookService stripePayoutWebhookService;

    @PostMapping("/payouts/webhook/stripe")
    public ResponseEntity<MessageResponseDto> handlePayoutWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        stripePayoutWebhookService.handlePayoutWebhook(payload, sigHeader);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(PayoutMessages.PAYOUT_WEBHOOK_RECEIVED));
    }

}
