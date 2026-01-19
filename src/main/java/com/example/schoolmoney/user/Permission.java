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
    PARENT_SCHOOL_CLASS_READ_ALL("parent:school:class:read:all"),

    PARENT_CHILDREN_FUND_READ_ALL("parent:children:fund:read:all"),

    PARENT_AVATAR_UPDATE("parent:avatar:update"),
    PARENT_AVATAR_READ("parent:avatar:read"),
    PARENT_AVATAR_DELETE("parent:avatar:delete"),

    CHILD_CREATE("child:create"),
    CHILD_READ("child:read"),
    CHILD_READ_ALL("child:read:all"),
    CHILD_UPDATE("child:update"),
    CHILD_DELETE("child:delete"),

    CHILD_SCHOOL_CLASS_JOIN("child:school:class:join"),
    CHILD_SCHOOL_CLASS_LEAVE("child:school:class:leave"),

    CHILD_AVATAR_UPDATE("child:avatar:update"),
    CHILD_AVATAR_READ("child:avatar:read"),
    CHILD_AVATAR_DELETE("child:avatar:delete"),

    CHILD_FUND_IGNORE("child:fund:ignore"),
    CHILD_FUND_UNIGNORE("child:fund:unignore"),

    SCHOOL_CLASS_CREATE("school:class:create"),
    SCHOOL_CLASS_READ("school:class:read"),
    SCHOOL_CLASS_READ_ALL("school:class:read:all"),
    SCHOOL_CLASS_UPDATE("school:class:update"),
    SCHOOL_CLASS_DELETE("school:class:delete"),

    SCHOOL_CLASS_FUND_READ_ALL("school:class:fund:read:all"),
    SCHOOL_CLASS_CHILDREN_READ_ALL("school:class:children:read:all"),

    SCHOOL_CLASS_INVITATION_CODE_REGENERATE("school:class:invitation:code:regenerate"),

    SCHOOL_CLASS_AVATAR_UPDATE("school:class:avatar:update"),
    SCHOOL_CLASS_AVATAR_READ("school:class:avatar:read"),
    SCHOOL_CLASS_AVATAR_DELETE("school:class:avatar:delete"),

    WALLET_BALANCE_READ("wallet:balance:read"),
    WALLET_INFO_READ("wallet:info:read"),

    WALLET_IBAN_SET("wallet:iban:set"),
    WALLET_IBAN_CLEAR("wallet:iban:clear"),

    WALLET_HISTORY_READ_ALL("wallet:history:read:all"),

    FUND_CREATE("fund:create"),
    FUND_READ("fund:read"),
    FUND_READ_ALL("fund:read:all"),
    FUND_UPDATE("fund:update"),
    FUND_DELETE("fund:delete"),
    FUND_CANCEL("fund:cancel"),
    FUND_CREATED_READ_ALL("fund:created:read:all"),

    FUND_LOGO_READ("fund:logo:read"),
    FUND_LOGO_UPDATE("fund:logo:update"),
    FUND_LOGO_DELETE("fund:logo:delete"),

    FUND_MEDIA_FILE_UPLOAD("fund:media:file:upload"),
    FUND_MEDIA_FILE_READ("fund:media:file:read"),
    FUND_MEDIA_METADATA_READ("fund:media:metadata:read"),
    FUND_MEDIA_METADATA_UPDATE("fund:media:metadata:update"),
    FUND_MEDIA_FILE_DELETE("fund:media:file:delete"),

    FUND_MEDIA_OPERATION_READ_ALL("fund:media:operation:read:all"),

    FUND_PAY("fund:pay"),
    FUND_WITHDRAW("fund:withdraw"),
    FUND_DEPOSIT("fund:deposit"),

    FUND_BLOCK("fund:block"),
    FUND_UNBLOCK("fund:unblock"),

    FUND_OPERATIONS_READ_ALL("fund:operations:read:all"),

    FUND_CHILDREN_STATUSES_READ_ALL("fund:children:statuses:read:all"),

    FUND_REPORT_GENERATE("fund:report:generate"),
    CHILD_REPORT_GENERATE("child:report:generate"),
    SCHOOL_CLASS_REPORT_GENERATE("school:class:report:generate"),

    FINANCIAL_OPERATION_HISTORY_READ("financial:operation:history:read"),

    WALLET_TOP_UP("wallet:top_up"),
    WALLET_WITHDRAWAL_INITIALIZE("wallet:withdrawal:initialize"),
    WALLET_WITHDRAWAL_INTERNAL_PERFORM("wallet:withdrawal:internal:perform"),

    ADMIN_ACCOUNT_CREATE("admin:account:create"),

    PARENT_CHILDREN_SCHOOL_CLASS_UNPAID_FUND_READ_ALL("parent:children:school:class:unpaid:fund:read:all"),
    PARENT_CHILDREN_FUND_HISTORY_READ_ALL("parent:children:fund:history:read:all"),
    PARENT_CHILDREN_UNPAID_FUND_READ_ALL("parent:children:unpaid:fund:read:all"),

    RANDOM_JOKE_READ("random:joke:read"),
    DAILY_JOKE_TURN_OFF("daily:joke:turn:off"),
    DAILY_JOKE_TURN_ON("daily:joke:turn:on"),
    DAILY_JOKE_TEST("daily:joke:test");

    private final String permission;

}
