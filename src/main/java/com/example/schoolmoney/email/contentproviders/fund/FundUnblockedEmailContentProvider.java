package com.example.schoolmoney.email.contentproviders.fund;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class FundUnblockedEmailContentProvider implements EmailContentProvider {

    private final String fundTitle;

    private final String schoolClassFullName;

    @Override
    public String getSubject() {
        return "Fund unblocked";
    }

    @Override
    public String getBody() {
        return "<p>We are happy to inform you that your <strong>" + fundTitle +
                "</strong> fund in <strong>" + schoolClassFullName + "</strong> class has been unblocked and is now active.</p>";
    }

}
