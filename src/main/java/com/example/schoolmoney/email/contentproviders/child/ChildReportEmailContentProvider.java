package com.example.schoolmoney.email.contentproviders.child;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChildReportEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final String childFullName;

    @Override
    public String build() {
        return "<p>Hi " + firstName + "!</p>" +
                "<p>Here is the copy of the generated report for your child, <i>" + childFullName + "</i>.</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message, please do not reply to this email.</i></p>";
    }

}
