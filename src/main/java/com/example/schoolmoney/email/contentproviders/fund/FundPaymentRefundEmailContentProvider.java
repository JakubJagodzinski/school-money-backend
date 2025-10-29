package com.example.schoolmoney.email.contentproviders.fund;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import com.example.schoolmoney.utils.AmountFormatter;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.Currency;

@Builder
@RequiredArgsConstructor
public class FundPaymentRefundEmailContentProvider implements EmailContentProvider {

    private final String fundTitle;

    private final String schoolClassFullName;

    private final String childFullName;

    private final long amountInCents;

    private final Currency currency;

    @Override
    public boolean isCritical() {
        return true;
    }

    @Override
    public String getSubject() {
        return "Fund payment refund";
    }

    @Override
    public String getBody() {
        return "<p>You have received a refund for your child, <strong>" + childFullName +
                "</strong>, from the <strong>" + fundTitle + "</strong> fund in <strong>" + schoolClassFullName + "</strong>.</p>" +
                "<p>An amount of <strong>" + AmountFormatter.format(amountInCents, currency) +
                "</strong> has been added to your wallet balance.</p>";
    }

}
