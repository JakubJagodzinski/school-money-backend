package com.example.schoolmoney.auth.access;

import com.example.schoolmoney.user.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPermission {
    Permission value();
}
