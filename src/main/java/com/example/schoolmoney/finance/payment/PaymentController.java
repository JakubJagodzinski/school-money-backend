package com.example.schoolmoney.finance.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

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

}
