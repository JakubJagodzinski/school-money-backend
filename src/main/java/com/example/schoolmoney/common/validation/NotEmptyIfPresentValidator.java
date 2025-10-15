package com.example.schoolmoney.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotEmptyIfPresentValidator implements ConstraintValidator<NotEmptyIfPresent, String> {

    private NotEmptyIfPresent annotation;

    @Override
    public void initialize(NotEmptyIfPresent annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(annotation.message()).addConstraintViolation();
            return false;
        }

        if (value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(annotation.notBlankMessage()).addConstraintViolation();
            return false;
        }

        return true;
    }

}
