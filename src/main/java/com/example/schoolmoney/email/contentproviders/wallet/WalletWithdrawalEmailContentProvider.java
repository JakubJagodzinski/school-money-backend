package com.example.schoolmoney.email.contentproviders.wallet;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import com.example.schoolmoney.utils.AmountFormatter;
import lombok.RequiredArgsConstructor;

import java.util.Currency;

@RequiredArgsConstructor
public class WalletWithdrawalEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final long amountInCents;

    private final Currency currency;

    @Override
    public String build() {
        return "<p>Hi " + firstName + ",</p>" +
                "<p>We’d like to inform you that a withdrawal of <strong>" + AmountFormatter.format(amountInCents, currency) +
                "</strong> from your wallet has been successfully processed to your linked bank account.</p>" +
                "<p>You can view the full operation history and your current wallet balance in the application.</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message, please do not reply to this email.</i></p>";
    }

}
