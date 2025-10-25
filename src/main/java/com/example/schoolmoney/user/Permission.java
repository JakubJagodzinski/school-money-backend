package com.example.schoolmoney.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {

    USER_PASSWORD_CHANGE("user:password:change"),
    USER_BLOCK("user:block"),
    USER_UNBLOCK("user:unblock"),

    PARENT_READ("parent:read"),
    PARENT_READ_ALL("parent:read:all"),
    PARENT_UPDATE("parent:update"),
    PARENT_DELETE("parent:delete"),

    PARENT_CHILDREN_READ_ALL("parent:children:read:all"),
    PARENT_SCHOOL_CLASS_READ_ALL("parent:school_class:read:all"),

    PARENT_CHILDREN_FUND_READ_ALL("parent_children_fund:read:all"),

    PARENT_AVATAR_UPDATE("parent:avatar:update"),
    PARENT_AVATAR_READ("parent:avatar:read"),
    PARENT_AVATAR_DELETE("parent:avatar:delete"),

    CHILD_CREATE("child:create"),
    CHILD_READ("child:read"),
    CHILD_READ_ALL("child:read:all"),
    CHILD_UPDATE("child:update"),
    CHILD_DELETE("child:delete"),
    CHILD_CLASS_JOIN("child:class:join"),

    CHILD_AVATAR_UPDATE("child:avatar:update"),
    CHILD_AVATAR_READ("child:avatar:read"),
    CHILD_AVATAR_DELETE("child:avatar:delete"),

    CHILD_FUND_IGNORE("child:fund:ignore"),
    CHILD_FUND_UNIGNORE("child:fund:unignore"),

    SCHOOL_CLASS_CREATE("school_class:create"),
    SCHOOL_CLASS_READ("school_class:read"),
    SCHOOL_CLASS_READ_ALL("school_class:read:all"),
    SCHOOL_CLASS_UPDATE("school_class:update"),
    SCHOOL_CLASS_DELETE("school_class:delete"),

    SCHOOL_CLASS_FUND_READ_ALL("school_class_fund:read:all"),
    SCHOOL_CLASS_CHILDREN_READ_ALL("school_class:children:read:all"),

    SCHOOL_CLASS_INVITATION_CODE_REGENERATE("school_class:invitation_code:regenerate"),

    SCHOOL_CLASS_AVATAR_UPDATE("school_class:avatar:update"),
    SCHOOL_CLASS_AVATAR_READ("school_class:avatar:read"),
    SCHOOL_CLASS_AVATAR_DELETE("school_class:avatar:delete"),

    WALLET_INFO_SET("wallet:info:set"),
    WALLET_INFO_READ("wallet:info:read"),
    WALLET_BALANCE_READ("wallet:balance:read"),
    WALLET_INFO_CLEAR("wallet:info:clear"),

    FUND_CREATE("fund:create"),
    FUND_READ("fund:read"),
    FUND_READ_ALL("fund:read:all"),
    FUND_UPDATE("fund:update"),
    FUND_DELETE("fund:delete"),
    FUND_CANCEL("fund:cancel"),
    FUND_CREATED_READ_ALL("fund_created:read:all"),

    FUND_LOGO_READ("fund:logo:read"),
    FUND_LOGO_UPDATE("fund:logo:update"),
    FUND_LOGO_DELETE("fund:logo:delete"),

    FUND_MEDIA_FILE_UPLOAD("fund_media:file:upload"),
    FUND_MEDIA_FILE_READ("fund_media:file:read"),
    FUND_MEDIA_METADATA_READ("fund_media:metadata:read"),
    FUND_MEDIA_METADATA_UPDATE("fund_media:metadata:update"),
    FUND_MEDIA_FILE_DELETE("fund_media:file:delete"),

    FUND_PAY("fund:pay"),
    FUND_WITHDRAW("fund:withdraw"),
    FUND_DEPOSIT("fund:deposit"),

    FUND_BLOCK("fund:block"),
    FUND_UNBLOCK("fund:unblock"),

    FUND_REPORT_GENERATE("fund:report:generate"),

    FINANCIAL_OPERATION_HISTORY_READ("financial_operation_history:read"),

    PAYMENT_SESSION_CREATE("payment_session:create"),

    ADMIN_ACCOUNT_CREATE("admin_account:create");

    private final String permission;

}
