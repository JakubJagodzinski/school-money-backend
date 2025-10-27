package com.example.schoolmoney.finance.payout.adapter;

import com.example.schoolmoney.finance.ProviderType;
import com.example.schoolmoney.finance.payout.dto.PayoutRequestDto;

public interface PayoutAdapter {

    ProviderType getProviderType();

    String createPayout(PayoutRequestDto payoutRequestDto) throws Exception;

}
