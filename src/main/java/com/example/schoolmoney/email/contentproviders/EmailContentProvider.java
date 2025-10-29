package com.example.schoolmoney.email.contentproviders;

public interface EmailContentProvider {

    default boolean isCritical() {
        return false;
    }

    String getSubject();

    default String getGreeting(String firstName) {
        return "<p>Hi " + firstName + ",</p>";
    }

    String getBody();

    default String getFooter() {
        return "<p>Best regards,<br>The SchoolMoney Team</p>" +
                "<p><i>Note: This is an automated message. Please do not reply to this email.</i></p>";
    }

    default String build(String firstName) {
        return getGreeting(firstName) + getBody() + getFooter();
    }

}
