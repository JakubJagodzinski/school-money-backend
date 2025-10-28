package com.example.schoolmoney.email.contentproviders.account;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountVerificationEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final String verificationLink;

    @Override
    public String build() {
        return "<p>Hi " + firstName + "!</p>" +
                "<p>Thank you for registering on our platform.</p>" +
                "<p>Click <a href=\"" + verificationLink + "\">here</a> to verify your account.</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message, please do not reply to this email.</i></p>";
    }

}
