package com.example.schoolmoney.email.contentproviders.wallet;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import com.example.schoolmoney.utils.AmountFormatter;
import lombok.RequiredArgsConstructor;

import java.util.Currency;

@RequiredArgsConstructor
public class WalletTopUpEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final long amountInCents;

    private final Currency currency;

    @Override
    public String build() {
        return "<p>Hi " + firstName + "!</p>" +
                "<p>Your wallet has been successfully topped up with an amount of " + AmountFormatter.format(amountInCents, currency) + ".</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message, please do not reply to this email.</i></p>";
    }

}
