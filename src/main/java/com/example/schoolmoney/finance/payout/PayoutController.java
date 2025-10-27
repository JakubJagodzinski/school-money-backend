package com.example.schoolmoney.finance.payout;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payouts")
public class PayoutController {

    @GetMapping("/status/success")
    public ResponseEntity<String> payoutSuccessRedirect() {
        String html = "<html>" +
                "<head><title>Payout successful</title></head>" +
                "<body>" +
                "<h1>Payout successful, you can close your browser window</h1>" +
                "</body>" +
                "</html>";

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @GetMapping("/status/failed")
    public ResponseEntity<String> payoutFailedRedirect() {
        String html = "<html>" +
                "<head><title>Payout failed</title></head>" +
                "<body>" +
                "<h1>Payout failed, you can close your browser window and try again</h1>" +
                "</body>" +
                "</html>";

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

}
