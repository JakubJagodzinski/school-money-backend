package com.example.schoolmoney.email.contentproviders.wallet;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import com.example.schoolmoney.utils.AmountFormatter;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.Currency;

@Builder
@RequiredArgsConstructor
public class WalletTopUpEmailContentProvider implements EmailContentProvider {

    private final long amountInCents;

    private final Currency currency;

    @Override
    public String getSubject() {
        return "Wallet top up";
    }

    @Override
    public String getBody() {
        return "<p>Your wallet has been successfully topped up with an amount of <strong>" + AmountFormatter.format(amountInCents, currency) + "</strong>.</p>";
    }

}
