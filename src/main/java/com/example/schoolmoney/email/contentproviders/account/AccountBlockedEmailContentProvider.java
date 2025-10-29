package com.example.schoolmoney.email.contentproviders.account;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import com.example.schoolmoney.utils.DateToStringConverter;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Builder
@RequiredArgsConstructor
public class AccountBlockedEmailContentProvider implements EmailContentProvider {

    private final String reason;

    private final long durationInDays;

    private final Instant blockedUntil;

    @Override
    public boolean isCritical() {
        return true;
    }

    @Override
    public String getSubject() {
        return "Account blocked";
    }

    @Override
    public String getBody() {
        String formattedReason = reason.toLowerCase().replaceAll("_", " ");

        if (blockedUntil == null) {
            return "<p>We are sorry to inform you that your account has been blocked permanently due to <strong>" + formattedReason + "</strong>.</p>" +
                    "<p>If you believe this is an error, please contact your school IT specialist.</p>";
        } else {
            return "<p>We are sorry to inform you that your account has been blocked for <strong>" + durationInDays + " days</strong> due to <strong>" + formattedReason + "</strong>.</p>" +
                    "<p>Your account will be unblocked automatically on <strong>" + DateToStringConverter.fromInstantToLocal(blockedUntil) + "</strong>.</p>";
        }
    }

}
