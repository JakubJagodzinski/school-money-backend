package com.example.schoolmoney.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {

    CHILD_CREATE("child:create"),
    CHILD_READ("child:read"),
    CHILD_UPDATE("child:update"),
    CHILD_DELETE("child:delete"),

    FUND_CREATE("fund:create"),
    FUND_READ("fund:read"),
    FUND_UPDATE("fund:update"),
    FUND_DELETE("fund:delete");

    private final String permission;

}
