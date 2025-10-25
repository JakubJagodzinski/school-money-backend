package com.example.schoolmoney.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateToStringConverter {

    private static final DateTimeFormatter fullFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter fileTimestampFormat = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");


    private DateToStringConverter() {
    }

    public static String fromInstant(Instant instant) {
        if (instant == null) {
            return "N/A";
        }

        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC")).format(fullFormat);
    }

    public static String fromInstantToLocal(Instant instant) {
        if (instant == null) {
            return "N/A";
        }

        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(fullFormat);
    }

    public static String nowFormatted() {
        return LocalDateTime.now().format(fullFormat);
    }

    public static String nowFileTimestamp() {
        return LocalDateTime.now().format(fileTimestampFormat);
    }

}
