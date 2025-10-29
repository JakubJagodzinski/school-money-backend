package com.example.schoolmoney.email.contentproviders.account;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class PasswordResetEmailContentProvider implements EmailContentProvider {

    private final String passwordResetUrl;

    @Override
    public String getSubject() {
        return "Password reset";
    }

    @Override
    public String getBody() {
        return "<p>You have requested password reset for your account</p>" +
                "<p>Click <a href=\"" + passwordResetUrl + "\">here</a> to set new password.</p>" +
                "<p>Password reset link will expire in an hour.</p>" +
                "<p>If you did not request password reset, please ignore this email.</p>";
    }

}
