package com.example.schoolmoney.utils;

import java.math.BigInteger;
import java.util.Random;

public class IbanUtil {

    private static final Random RANDOM = new Random();

    private IbanUtil() {
    }

    /**
     * Generates a random Polish IBAN with correct check digits.
     * Format: PLkkBBBBBBBBCCCCCCCCCCCCCCCC (28 characters)
     * where 'kk' = check digits, 'B' = bank, 'C' = account numbers
     */
    public static String generateRandomPlIban() {
        String countryCode = "PL";

        StringBuilder sb = new StringBuilder();
        sb.append(countryCode);
        sb.append("00"); // placeholder for check digits

        for (int i = 0; i < 24; i++) {
            sb.append(RANDOM.nextInt(10));
        }

        String checkDigits = calculateCheckDigits(sb.toString());
        sb.replace(2, 4, checkDigits);

        return sb.toString();
    }

    /**
     * Masks an IBAN for display.
     * Shows first 4 and last 4 characters; the rest is replaced by asterisks.
     */
    public static String maskIban(String iban) {
        if (iban == null || iban.length() < 8) {
            return "****";
        }

        String start = iban.substring(0, 4);
        String end = iban.substring(iban.length() - 4);

        return start + " **** **** **** **** " + end;
    }

    /**
     * Calculates the IBAN check digits according to ISO 13616.
     */
    private static String calculateCheckDigits(String iban) {
        // Move country code and check digits to the end
        String rearranged = iban.substring(4) + iban.substring(0, 4);

        // Convert letters to numbers (A=10, B=11, ..., Z=35)
        StringBuilder numericIban = new StringBuilder();
        for (char ch : rearranged.toCharArray()) {
            if (Character.isLetter(ch)) {
                numericIban.append(Character.getNumericValue(ch));
            } else {
                numericIban.append(ch);
            }
        }

        // Mod 97 calculation
        BigInteger num = new BigInteger(numericIban.toString());
        int mod97 = num.mod(BigInteger.valueOf(97)).intValue();

        // Check digits = 98 - mod97
        int check = 98 - mod97;
        return String.format("%02d", check);
    }

}
