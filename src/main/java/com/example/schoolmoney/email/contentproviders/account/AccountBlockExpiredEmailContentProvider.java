package com.example.schoolmoney.email.contentproviders.account;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class AccountBlockExpiredEmailContentProvider implements EmailContentProvider {

    @Override
    public String getSubject() {
        return "Account block expired";
    }

    @Override
    public String getBody() {
        return "<p>We're happy to inform you that your account block has expired and your account is now active.</p>" +
                "<p>Please ensure you follow the system guidelines to avoid future blocks.</p>";
    }

}
