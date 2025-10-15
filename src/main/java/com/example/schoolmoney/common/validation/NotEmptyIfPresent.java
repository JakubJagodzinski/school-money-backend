package com.example.schoolmoney.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NotEmptyIfPresentValidator.class)
public @interface NotEmptyIfPresent {

    String message() default "Field cannot be empty when passed";

    String notBlankMessage() default "Field cannot contain only whitespaces when passed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
