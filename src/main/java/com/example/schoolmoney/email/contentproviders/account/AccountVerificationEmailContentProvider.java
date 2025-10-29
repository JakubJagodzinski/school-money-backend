package com.example.schoolmoney.email.contentproviders.account;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class AccountVerificationEmailContentProvider implements EmailContentProvider {

    private final String verificationLink;

    @Override
    public String getSubject() {
        return "Account verification";
    }

    @Override
    public String getBody() {
        return "<p>Thank you for registering on our platform.</p>" +
                "<p>Click <a href=\"" + verificationLink + "\">here</a> to verify your account.</p>";
    }

}
