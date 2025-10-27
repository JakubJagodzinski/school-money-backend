package com.example.schoolmoney.finance.payout;

import com.example.schoolmoney.common.constants.messages.PaymentMessages;
import com.example.schoolmoney.common.constants.messages.PayoutMessages;
import com.example.schoolmoney.finance.ProviderType;
import com.example.schoolmoney.finance.payout.adapter.PayoutAdapter;
import com.example.schoolmoney.finance.payout.dto.PayoutRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayoutService {

    private final List<PayoutAdapter> payoutAdapters;

    public String createPayout(PayoutRequestDto payoutRequestDto) throws IllegalStateException {
        PayoutAdapter adapter = getAdapter(payoutRequestDto.getProviderType());

        try {
            return adapter.createPayout(payoutRequestDto);
        } catch (Exception e) {
            throw new IllegalStateException(PaymentMessages.PAYMENT_SESSION_CREATION_ERROR);
        }
    }

    private PayoutAdapter getAdapter(ProviderType providerType) throws IllegalArgumentException {
        return payoutAdapters.stream()
                .filter(a -> a.getProviderType().equals(providerType))
                .findFirst()
                .orElseThrow(() -> {
                    log.error(PayoutMessages.UNSUPPORTED_PAYOUT_PROVIDER);
                    return new IllegalArgumentException(PayoutMessages.UNSUPPORTED_PAYOUT_PROVIDER);
                });
    }

}
