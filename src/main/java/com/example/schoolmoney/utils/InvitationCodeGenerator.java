package com.example.schoolmoney.utils;

import java.security.SecureRandom;

public final class InvitationCodeGenerator {

    private static final String CHAR_POOL = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // without I, O, 1, 0

    private static final int DEFAULT_CODE_LENGTH = 12;

    private static final int MIN_CODE_LENGTH = 12;

    public static final int MAX_CODE_LENGTH = 12;

    private static final SecureRandom RANDOM = new SecureRandom();

    private InvitationCodeGenerator() {
    }

    public static String generate() {
        return generate(DEFAULT_CODE_LENGTH);
    }

    public static String generate(int codeLength) {
        if (codeLength < MIN_CODE_LENGTH) {
            throw new IllegalArgumentException("Code length must be at least " + MIN_CODE_LENGTH);
        }

        if (codeLength > MAX_CODE_LENGTH) {
            throw new IllegalArgumentException("Code length must be at most " + MAX_CODE_LENGTH);
        }

        StringBuilder sb = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            sb.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
        }

        return sb.toString();
    }

}
