package com.example.schoolmoney.email.contentproviders.account;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class AccountUnblockedEmailContentProvider implements EmailContentProvider {

    private final String reason;

    @Override
    public String getSubject() {
        return "Account unblocked";
    }

    @Override
    public String getBody() {
        String formattedReason = reason.toLowerCase().replaceAll("_", " ");
        return "<p>We'd like to inform you that your account has been unblocked due to <strong>" + formattedReason + "</strong>.</p>";
    }

}
