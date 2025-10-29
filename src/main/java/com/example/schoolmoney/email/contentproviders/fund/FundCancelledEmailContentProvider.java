package com.example.schoolmoney.email.contentproviders.fund;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class FundCancelledEmailContentProvider implements EmailContentProvider {

    private final String fundTitle;

    private final String schoolClassFullName;

    @Override
    public String getSubject() {
        return "Fund cancelled";
    }

    @Override
    public String getBody() {
        return "<p>We’d like to inform you that the <strong>" + fundTitle + "</strong> fund in <strong>" + schoolClassFullName +
                "</strong> has been cancelled by the class treasurer.</p>" +
                "<p>If you made a contribution to this fund for your child, you will receive another email confirming the refund once it has been processed.</p>";
    }

}
