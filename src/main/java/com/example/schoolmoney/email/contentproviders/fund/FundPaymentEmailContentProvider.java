package com.example.schoolmoney.email.contentproviders.fund;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import com.example.schoolmoney.utils.AmountFormatter;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.Currency;

@Builder
@RequiredArgsConstructor
public class FundPaymentEmailContentProvider implements EmailContentProvider {

    private final String fundTitle;

    private final String schoolClassFullName;

    private final String childFullName;

    private final long amountInCents;

    private final Currency currency;

    @Override
    public String getSubject() {
        return "Fund payment";
    }

    @Override
    public String getBody() {
        return "<p>You have successfully paid for your child, <strong>" + childFullName +
                "</strong>, to the <strong>" + fundTitle + "</strong> fund in <strong>" + schoolClassFullName + "</strong>.</p>" +
                "<p>Your wallet has been charged <strong>" + AmountFormatter.format(amountInCents, currency) + "</strong>.</p>";
    }

}