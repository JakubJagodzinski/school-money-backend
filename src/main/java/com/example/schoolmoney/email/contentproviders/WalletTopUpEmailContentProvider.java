package com.example.schoolmoney.email.contentproviders;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WalletTopUpEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final long amountInCents;

    @Override
    public String build() {
        return "<p>Hi " + firstName + "!</p>" +
                "<p>Your wallet has been successfully topped up with an amount of " + String.format("%.2f", amountInCents / 100.0) + ".</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>";
    }

}
