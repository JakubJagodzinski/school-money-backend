package com.example.schoolmoney.email.contentproviders.wallet;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import com.example.schoolmoney.utils.AmountFormatter;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.Currency;

@Builder
@RequiredArgsConstructor
public class WalletWithdrawalEmailContentProvider implements EmailContentProvider {

    private final long amountInCents;

    private final Currency currency;

    @Override
    public String getSubject() {
        return "Wallet withdrawal";
    }

    @Override
    public String getBody() {
        return "<p>We’d like to inform you that a withdrawal of <strong>" + AmountFormatter.format(amountInCents, currency) +
                "</strong> from your wallet has been successfully processed to your linked bank account.</p>" +
                "<p>You can view the full operation history and your current wallet balance in the application.</p>";
    }

}
