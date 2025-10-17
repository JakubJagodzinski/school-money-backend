package com.example.schoolmoney.email.contentproviders;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VerificationEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final String verificationLink;

    @Override
    public String build() {
        return "<p>Hi " + firstName + "!</p>" +
                "<p>Thank you for registering on our platform.</p>" +
                "<p>Click <a href=\"" + verificationLink + "\">here</a> to verify your account.</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>";
    }

}
