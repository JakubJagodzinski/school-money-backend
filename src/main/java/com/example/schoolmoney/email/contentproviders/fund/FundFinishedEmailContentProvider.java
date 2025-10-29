package com.example.schoolmoney.email.contentproviders.fund;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class FundFinishedEmailContentProvider implements EmailContentProvider {

    private final String fundTitle;

    private final String schoolClassFullName;

    @Override
    public String getSubject() {
        return "Fund finished";
    }

    @Override
    public String getBody() {
        return "<p>We'd like to inform you that the <strong>" + fundTitle + "</strong> fund in <strong>" + schoolClassFullName + "</strong> class has finished.</p>" +
                "<p>You can view the full operation history in the application.</p>";
    }

}
