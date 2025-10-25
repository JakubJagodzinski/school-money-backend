package com.example.schoolmoney.email.contentproviders;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FundUnblockedEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final String fundTitle;

    private final String schoolClassFullName;

    @Override
    public String build() {
        return "<p>Hi " + firstName + ",</p>" +
                "<p>We are happy to inform you that your " + fundTitle + " fund in " + schoolClassFullName + " class has been unblocked and is now active.</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message, please do not reply to this email.</i></p>";
    }

}
