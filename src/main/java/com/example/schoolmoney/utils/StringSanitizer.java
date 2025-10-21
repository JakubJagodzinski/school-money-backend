package com.example.schoolmoney.utils;

import com.ibm.icu.text.Transliterator;

public class StringSanitizer {

    private StringSanitizer() {
    }

    private static final Transliterator TO_ASCII = Transliterator.getInstance("Any-Latin; Latin-ASCII");

    public static String sanitizeString(String input) {
        String transliterated = TO_ASCII.transliterate(input);
        return transliterated.replaceAll("[^\\p{ASCII}a-zA-Z0-9_.-]", "_");
    }

}
