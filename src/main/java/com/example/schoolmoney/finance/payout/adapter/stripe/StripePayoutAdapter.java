package com.example.schoolmoney.finance.payout.adapter.stripe;

import com.example.schoolmoney.finance.ProviderType;
import com.example.schoolmoney.finance.payout.adapter.PayoutAdapter;
import com.example.schoolmoney.finance.payout.dto.PayoutRequestDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Payout;
import com.stripe.param.PayoutCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StripePayoutAdapter implements PayoutAdapter {

    private final StripePayoutConfig stripePayoutConfig;

    @PostConstruct
    void init() {
        Stripe.apiKey = stripePayoutConfig.getApiKey();
    }

    @Override
    public ProviderType getProviderType() {
        return ProviderType.STRIPE;
    }

    @Override
    public String createPayout(PayoutRequestDto payoutRequestDto) throws StripeException {
        String testBankAccountId = "ba_1SMeZOB0CbUcnXg4BQEhSBub";

        PayoutCreateParams params = PayoutCreateParams.builder()
                .setAmount(payoutRequestDto.getAmountInCents())
                .setCurrency(payoutRequestDto.getCurrency().toString())
                .setDestination(testBankAccountId)
                .setStatementDescriptor(payoutRequestDto.getPayoutName())
                .putMetadata("operationId", payoutRequestDto.getOperationId().toString())
                .putMetadata("userId", payoutRequestDto.getUserId().toString())
                .build();

        Payout payout = Payout.create(params);

        return payout.getId();
    }

}
