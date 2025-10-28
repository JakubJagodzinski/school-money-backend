package com.example.schoolmoney.email.contentproviders.account;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChangeEmailConfirmationEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final String verificationLink;

    @Override
    public String build() {
        return "<p>Hi " + firstName + "!</p>" +
                "<p>We received a request to change the email address associated with your account.</p>" +
                "<p>Click <a href=\"" + verificationLink + "\">here</a> to confirm your new email.</p>" +
                "<p>If you didn’t request this change, please ignore this message. Your account will remain unchanged.</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message, please do not reply to this email.</i></p>";
    }

}
