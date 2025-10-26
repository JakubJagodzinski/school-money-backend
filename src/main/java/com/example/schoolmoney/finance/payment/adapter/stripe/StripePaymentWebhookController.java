package com.example.schoolmoney.finance.payment.adapter.stripe;

import com.example.schoolmoney.common.constants.messages.PaymentMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class StripePaymentWebhookController {

    private final StripePaymentWebhookService stripePaymentWebhookService;

    @PostMapping("/payments/webhook/stripe")
    public ResponseEntity<MessageResponseDto> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        stripePaymentWebhookService.handlePaymentWebhook(payload, sigHeader);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(PaymentMessages.PAYMENT_WEBHOOK_RECEIVED));
    }

}
