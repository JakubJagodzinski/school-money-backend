package com.example.schoolmoney.email.contentproviders.account;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Builder
@RequiredArgsConstructor
public class LongAbsenceEmailContentProvider implements EmailContentProvider {

    private final Instant lastOnlineAt;

    @Override
    public boolean isCritical() {
        return true;
    }

    @Override
    public String getSubject() {
        return "We miss you!";
    }

    @Override
    public String getBody() {
        return "<p>It's been so long since you last logged in to our system...</p>" +
                "<p>The last time you were online was on " + lastOnlineAt.toString() + ".</p>" +
                "<p>We miss you! Maybe you'd like to see what's new in our app?</p>";
    }

}
