package com.example.schoolmoney.utils;

import java.util.Currency;

public class AmountFormatter {

    private AmountFormatter() {
    }

    public static String format(long amountInCents, Currency currency) {
        try {
            double amount = amountInCents / 100.0;
            return String.format("%.2f %s", amount, currency);
        } catch (NumberFormatException e) {
            return amountInCents + " " + currency;
        }
    }

}
