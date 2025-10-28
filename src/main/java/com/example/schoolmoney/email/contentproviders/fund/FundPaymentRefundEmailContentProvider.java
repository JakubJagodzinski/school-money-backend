package com.example.schoolmoney.email.contentproviders.fund;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import com.example.schoolmoney.utils.AmountFormatter;
import lombok.RequiredArgsConstructor;

import java.util.Currency;

@RequiredArgsConstructor
public class FundPaymentRefundEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final String fundTitle;

    private final String schoolClassFullName;

    private final String childFullName;

    private final long amountInCents;

    private final Currency currency;

    @Override
    public String build() {
        return "<p>Hi " + firstName + ",</p>" +
                "<p>You have received a refund for your child, " + childFullName +
                ", from the <strong>" + fundTitle + "</strong> fund in " + schoolClassFullName + ".</p>" +
                "<p>An amount of <strong>" + AmountFormatter.format(amountInCents, currency) +
                "</strong> has been returned to your wallet balance.</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message. Please do not reply to this email.</i></p>";
    }

}
