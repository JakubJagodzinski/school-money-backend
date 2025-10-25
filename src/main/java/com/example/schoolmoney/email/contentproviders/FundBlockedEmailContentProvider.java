package com.example.schoolmoney.email.contentproviders;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FundBlockedEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final String fundTitle;

    private final String schoolClassFullName;

    @Override
    public String build() {
        return "<p>Hi " + firstName + ",</p>" +
                "<p>We are sorry to inform you that your " + fundTitle + " fund in " + schoolClassFullName + " class has been blocked.</p>" +
                "<p>If you believe this is an error, please contact your school IT specialist.</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message, please do not reply to this email.</i></p>";
    }

}
