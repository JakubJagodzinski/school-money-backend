package com.example.schoolmoney.finance.payout.adapter.stripe;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "stripe")
public class StripePayoutConfig {

    private String apiKey;

    private String payoutWebhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

}
