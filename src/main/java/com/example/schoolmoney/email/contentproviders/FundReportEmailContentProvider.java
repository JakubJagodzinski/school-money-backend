package com.example.schoolmoney.email.contentproviders;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FundReportEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final String fundTitle;

    @Override
    public String build() {
        return "<p>Hi " + firstName + "!</p>" +
                "<p>Here is the copy of the generated report for the <i>" + fundTitle + "</i> fund.</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message, please do not reply to this email.</i></p>";
    }

}
