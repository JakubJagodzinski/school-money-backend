package com.example.schoolmoney.utils;

public class IbanMasker {

    private IbanMasker() {
    }

    public static String maskIban(String iban) {
        if (iban == null || iban.length() < 4) {
            return "****";
        }
        return "****" + iban.substring(iban.length() - 4);
    }

}
