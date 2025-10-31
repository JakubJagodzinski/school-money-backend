package com.example.schoolmoney.email.contentproviders.fund;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class FundCreatedEmailContentProvider implements EmailContentProvider {

    private final String fundAuthorFullName;

    private final String fundTitle;

    private final String schoolClassFullName;

    @Override
    public String getSubject() {
        return "New fund";
    }

    @Override
    public String getBody() {
        return "<p>A new fund titled <strong>" + fundTitle + "</strong> has been created by <strong>"
                + fundAuthorFullName + "</strong> for the <strong>" + schoolClassFullName + "</strong> class.</p>"
                + "<p>You can now view the fund and pay for your child in the application.</p>";
    }

}
