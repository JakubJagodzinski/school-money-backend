package com.example.schoolmoney.email.contentproviders.misc;

import com.example.schoolmoney.email.contentproviders.EmailContentProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class WeekendEmailContentProvider implements EmailContentProvider {

    @Override
    public String getSubject() {
        return "Weekend mode activated";
    }

    @Override
    public String getBody() {
        return """
                <p><strong>Finally… IT’S FRIDAY! 🎉</strong></p>
                
                <p>
                    Another week with
                    <a href="https://www.youtube.com/watch?v=dQw4w9WgXcQ"
                       title="Fun fact: this email only works on Fridays 😎"
                       target="_blank" rel="noopener noreferrer">
                        <strong>SchoolMoney</strong>
                    </a>
                    is done and dusted.
                </p>
                
                <p>
                    Time to close the laptop, forget about tasks for a while,
                    and switch to <em>weekend mode</em> 😎
                </p>
                
                <p>Enjoy your weekend and see you on Monday!</p>
                """;
    }

}
