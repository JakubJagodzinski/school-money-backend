package com.example.schoolmoney.email.contentproviders;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResetPasswordEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final String passwordResetUrl;

    @Override
    public String build() {
        return "<p>Hi " + firstName + "!</p>" +
                "<p>You have requested password reset for your account</p>" +
                "<p>Click <a href=\"" + passwordResetUrl + "\">here</a> to set new password.</p>" +
                "<p>Password reset link will expire in an hour.</p>" +
                "<p>If you did not request password reset, please ignore this email.</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message, please do not reply to this email.</i></p>";
    }

}
