package com.example.schoolmoney.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomBase64TokenGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int DEFAULT_BYTE_LENGTH = 16; // 128-bits

    private RandomBase64TokenGenerator() {
    }

    public static String generate() {
        return generate(DEFAULT_BYTE_LENGTH);
    }

    public static String generate(int byteLength) {
        byte[] randomBytes = new byte[byteLength];
        RANDOM.nextBytes(randomBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

}
