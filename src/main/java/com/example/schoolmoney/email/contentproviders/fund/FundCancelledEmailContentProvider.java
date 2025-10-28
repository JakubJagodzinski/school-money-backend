package com.example.schoolmoney.email.contentproviders.fund;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FundCancelledEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final String fundTitle;

    private final String schoolClassFullName;

    @Override
    public String build() {
        return "<p>Hi " + firstName + ",</p>" +
                "<p>We’d like to inform you that the <strong>" + fundTitle + "</strong> fund in " + schoolClassFullName +
                " has been cancelled by the class treasurer.</p>" +
                "<p>If you made a contribution to this fund for your child, you will receive another email confirming the refund once it has been processed.</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message. Please do not reply to this email.</i></p>";
    }

}
