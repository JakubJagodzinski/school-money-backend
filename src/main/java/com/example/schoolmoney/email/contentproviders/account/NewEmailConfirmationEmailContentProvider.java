package com.example.schoolmoney.email.contentproviders.account;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class NewEmailConfirmationEmailContentProvider implements EmailContentProvider {

    private final String newEmailConfirmationLink;

    @Override
    public boolean isCritical() {
        return true;
    }

    @Override
    public String getSubject() {
        return "New email confirmation";
    }

    @Override
    public String getBody() {
        return "<p>We received a request to change the email address associated with your account.</p>" +
                "<p>Click <a href=\"" + newEmailConfirmationLink + "\">here</a> to confirm your new email.</p>" +
                "<p>If you didn’t request this change, please ignore this message. Your account will remain unchanged.</p>";
    }

}
