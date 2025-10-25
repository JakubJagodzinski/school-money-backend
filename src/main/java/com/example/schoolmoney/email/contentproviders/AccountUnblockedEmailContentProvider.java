package com.example.schoolmoney.email.contentproviders;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountUnblockedEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final String reason;

    @Override
    public String build() {
        String formattedReason = reason.toLowerCase().replaceAll("_", " ");

        return "<p>Hi " + firstName + ",</p>" +
                "<p>We'd like to inform you that your account has been unblocked due to " + formattedReason + ".</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message, please do not reply to this email.</i></p>";
    }

}
