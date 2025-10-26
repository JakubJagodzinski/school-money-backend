package com.example.schoolmoney.email.contentproviders;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FundFinishedEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final String fundTitle;

    private final String schoolClassFullName;

    @Override
    public String build() {
        return "<p>Hi " + firstName + ",</p>" +
                "<p>The " + fundTitle + " fund in " + schoolClassFullName + " class has finished.</p>" +
                "<p>You can view the full operation history in the application.</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message, please do not reply to this email.</i></p>";
    }

}
