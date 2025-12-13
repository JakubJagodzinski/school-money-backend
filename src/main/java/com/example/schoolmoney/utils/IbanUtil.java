package com.example.schoolmoney.utils;

import java.util.Random;

public class IbanUtil {

    private static final Random RANDOM = new Random();
    private static final int IBAN_LENGTH = 34;

    private IbanUtil() {
    }

    public static String generateRandomIban() {
        StringBuilder sb = new StringBuilder(IBAN_LENGTH);
        for (int i = 0; i < IBAN_LENGTH; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public static String maskIban(String iban) {
        if (iban == null || iban.length() < 4) {
            return "****";
        }
        return "****" + iban.substring(iban.length() - 4);
    }

}
