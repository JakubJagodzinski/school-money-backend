package com.example.schoolmoney.email.contentproviders.fund;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class FundReportEmailContentProvider implements EmailContentProvider {

    private final String fundTitle;

    @Override
    public String getSubject() {
        return "Fund financial report";
    }

    @Override
    public String getBody() {
        return "<p>Here is the copy of the generated financial report for the <strong>" + fundTitle + "</strong> fund.</p>";
    }

}
