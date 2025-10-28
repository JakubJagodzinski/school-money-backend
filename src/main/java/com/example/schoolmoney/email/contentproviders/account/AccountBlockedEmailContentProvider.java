package com.example.schoolmoney.email.contentproviders.account;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import com.example.schoolmoney.utils.DateToStringConverter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@RequiredArgsConstructor
public class AccountBlockedEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final String reason;

    private final long durationInDays;

    private final Instant blockedUntil;

    @Override
    public String build() {
        String formattedReason = reason.toLowerCase().replaceAll("_", " ");
        String firstParagraph;
        String secondParagraph;
        String formattedBlockedUntil = DateToStringConverter.fromInstantToLocal(blockedUntil);

        if (blockedUntil == null) {
            firstParagraph = "<p>We are sorry to inform you that your account has been blocked permanently due to " + formattedReason + ".</p>";
            secondParagraph = "<p>If you believe this is an error, please contact your school IT specialist.</p>";
        } else {
            firstParagraph = "<p>We are sorry to inform you that your account has been blocked for " + durationInDays + " days due to " + formattedReason + ".</p>";
            secondParagraph = "<p>Your account will be unblocked automatically on <strong>" + formattedBlockedUntil + "</strong>.</p>";
        }

        return "<p>Hi " + firstName + ",</p>" +
                firstParagraph +
                secondParagraph +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message, please do not reply to this email.</i></p>";
    }

}
