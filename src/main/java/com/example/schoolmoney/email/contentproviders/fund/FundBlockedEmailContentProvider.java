package com.example.schoolmoney.email.contentproviders.fund;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class FundBlockedEmailContentProvider implements EmailContentProvider {

    private final String fundTitle;

    private final String schoolClassFullName;

    @Override
    public boolean isCritical() {
        return true;
    }

    @Override
    public String getSubject() {
        return "Fund blocked";
    }

    @Override
    public String getBody() {
        return "<p>We are sorry to inform you that your <strong>" + fundTitle + "</strong> fund in <strong>" + schoolClassFullName + "</strong> class has been blocked.</p>" +
                "<p>If you believe this is an error, please contact your school IT specialist.</p>";
    }

}
