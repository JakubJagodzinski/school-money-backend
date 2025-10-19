package com.example.schoolmoney.payment;

import com.example.schoolmoney.common.constants.messages.PaymentMessages;
import com.example.schoolmoney.common.dto.MessageResponseDto;
import com.example.schoolmoney.payment.dto.PaymentSessionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/session/top-up")
    public ResponseEntity<PaymentSessionDto> createStripePaymentSession(@RequestParam PaymentProviderType paymentProviderType, @RequestParam long amountInCents) {
        PaymentSessionDto paymentSessionDto = paymentService.createPaymentSession(paymentProviderType, amountInCents);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentSessionDto);
    }

    @GetMapping("/status/success")
    public ResponseEntity<String> paymentSuccessRedirect() {
        String html = "<html>" +
                "<head><title>Payment successful</title></head>" +
                "<body>" +
                "<h1>Payment successful, you can close your browser window</h1>" +
                "</body>" +
                "</html>";

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @GetMapping("/status/failed")
    public ResponseEntity<String> paymentFailedRedirect() {
        String html = "<html>" +
                "<head><title>Payment failed</title></head>" +
                "<body>" +
                "<h1>Payment failed, you can close your browser window and try again</h1>" +
                "</body>" +
                "</html>";

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @PostMapping("/webhook/stripe")
    public ResponseEntity<MessageResponseDto> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        paymentService.handleWebhook(PaymentProviderType.STRIPE, payload, sigHeader);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto(PaymentMessages.PAYMENT_WEBHOOK_RECEIVED));
    }

}
