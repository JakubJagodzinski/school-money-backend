package com.example.schoolmoney.email.contentproviders;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountBlockExpiredEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    @Override
    public String build() {
        return "<p>Hi " + firstName + ",</p>" +
                "<p>We're happy to inform you that your account block has expired and your account is now active.</p>" +
                "<p>Please ensure you follow the system guidelines to avoid future blocks.</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message, please do not reply to this email.</i></p>";
    }

}
