package com.example.schoolmoney.email.contentproviders.misc;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class DailyJokeEmailContentProvider implements EmailContentProvider {

    private final String joke;

    @Override
    public String getSubject() {
        return "Daily joke";
    }

    @Override
    public String getBody() {
        return """
                <p>Here's a little laugh to start your day:</p>
                <blockquote style="font-style: italic; color: #555;">%s</blockquote>
                <p>Take a sip of coffee, smile, and enjoy your morning!</p>
                """.formatted(joke);
    }

}
