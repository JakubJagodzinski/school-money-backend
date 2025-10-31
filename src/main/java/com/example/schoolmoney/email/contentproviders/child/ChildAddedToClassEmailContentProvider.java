package com.example.schoolmoney.email.contentproviders.child;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class ChildAddedToClassEmailContentProvider implements EmailContentProvider {

    private final String childFullName;

    private final String schoolClassFullName;

    @Override
    public String getSubject() {
        return "Child added to class";
    }

    @Override
    public String getBody() {
        return "<p>Your child <strong>" + childFullName + "</strong> has been successfully added to the class <strong>" + schoolClassFullName + "</strong>.</p>"
                + "<p>You can now view and participate in funds for this class.</p>";
    }

}
