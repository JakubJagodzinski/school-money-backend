package com.example.schoolmoney.email.contentproviders;

import com.example.schoolmoney.common.constants.messages.UserMessages;
import com.example.schoolmoney.user.Role;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrationEmailContentProvider implements EmailContentProvider {

    private final String firstName;

    private final Role role;

    private final String verificationLink;

    @Override
    public String build() {
        String intro;

        if (role == Role.PARENT) {
            intro = "Thank you for registering on our platform. " +
                    "Please verify your email to complete your account.";
        } else {
            throw new IllegalArgumentException(UserMessages.INVALID_USER_ROLE);
        }

        return "<p>Hi " + firstName + ",</p>" +
                "<p>" + intro + "</p>" +
                "<p>Click <a href=\"" + verificationLink + "\">here</a> to verify your account.</p>" +
                "<p>Best regards,<br>The SchoolMoney Team</p>";
    }

}
