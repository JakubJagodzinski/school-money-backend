package com.example.schoolmoney.utils;

public class SnakeCaseConverter {

    private SnakeCaseConverter() {
    }

    public static String convertToSnakeCase(String field) {
        return field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

}
