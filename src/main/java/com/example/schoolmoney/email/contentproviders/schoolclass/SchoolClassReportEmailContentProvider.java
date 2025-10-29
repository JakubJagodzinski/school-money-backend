package com.example.schoolmoney.email.contentproviders.schoolclass;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class SchoolClassReportEmailContentProvider implements EmailContentProvider {

    private final String schoolClassFullName;

    @Override
    public String getSubject() {
        return "School class financial report";
    }

    @Override
    public String getBody() {
        return "<p>Here is the copy of the generated report for the <strong>" + schoolClassFullName + "</strong> school class.</p>";
    }

}
