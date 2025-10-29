package com.example.schoolmoney.email.contentproviders.child;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class ChildReportEmailContentProvider implements EmailContentProvider {

    private final String childFullName;

    @Override
    public String getSubject() {
        return "Your child financial report";
    }

    @Override
    public String getBody() {
        return "<p>Here is the copy of the generated report for your child, <strong>" + childFullName + "</strong>.</p>";
    }

}
