package com.example.schoolmoney.email;

import java.time.LocalTime;

public class GreetingUtil {

    public static String getGreeting(String firstName) {
        LocalTime now = LocalTime.now();
        String greeting;

        if (now.isBefore(LocalTime.of(12, 0))) {
            greeting = "Good morning";
        } else if (now.isBefore(LocalTime.of(18, 0))) {
            greeting = "Good afternoon";
        } else {
            greeting = "Good evening";
        }

        return "<strong>" + greeting + ", " + firstName + "!</strong>";
    }

}
