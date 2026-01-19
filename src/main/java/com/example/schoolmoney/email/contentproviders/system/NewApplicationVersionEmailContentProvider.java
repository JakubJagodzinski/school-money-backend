package com.example.schoolmoney.email.contentproviders.system;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Builder
@RequiredArgsConstructor
public class NewApplicationVersionEmailContentProvider implements EmailContentProvider {

    private final String applicationVersion;

    private final List<String> changelog;

    @Override
    public boolean isCritical() {
        return true;
    }

    @Override
    public String getSubject() {
        return "New application version " + applicationVersion + "!";
    }

    @Override
    public String getBody() {
        return "<p>New application version " + applicationVersion + " is available!</p>" +
                "<p>Here is the changelog:</p>" +
                "<ul>" + changelog.stream().map(line -> "<li>" + line + "</li>").reduce("", String::concat) + "</ul>" +
                "<p>Don't wait any longer and check all the new features by yourself!</p>";
    }

}
