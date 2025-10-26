package com.example.schoolmoney.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Role {

    PARENT(
            false,
            Set.of(
                    Permission.USER_PASSWORD_CHANGE,

                    Permission.PARENT_READ,
                    Permission.PARENT_UPDATE,
                    Permission.PARENT_DELETE,

                    Permission.PARENT_CHILDREN_READ_ALL,
                    Permission.PARENT_SCHOOL_CLASS_READ_ALL,

                    Permission.PARENT_CHILDREN_FUND_READ_ALL,

                    Permission.PARENT_AVATAR_UPDATE,
                    Permission.PARENT_AVATAR_READ,
                    Permission.PARENT_AVATAR_DELETE,

                    Permission.CHILD_CREATE,
                    Permission.CHILD_READ,
                    Permission.CHILD_READ_ALL,
                    Permission.CHILD_UPDATE,
                    Permission.CHILD_DELETE,
                    Permission.CHILD_CLASS_JOIN,

                    Permission.CHILD_AVATAR_UPDATE,
                    Permission.CHILD_AVATAR_READ,
                    Permission.CHILD_AVATAR_DELETE,

                    Permission.CHILD_FUND_IGNORE,
                    Permission.CHILD_FUND_UNIGNORE,

                    Permission.SCHOOL_CLASS_CREATE,
                    Permission.SCHOOL_CLASS_READ,
                    Permission.SCHOOL_CLASS_UPDATE,
                    Permission.SCHOOL_CLASS_DELETE,

                    Permission.SCHOOL_CLASS_FUND_READ_ALL,
                    Permission.SCHOOL_CLASS_CHILDREN_READ_ALL,

                    Permission.SCHOOL_CLASS_INVITATION_CODE_REGENERATE,

                    Permission.SCHOOL_CLASS_AVATAR_UPDATE,
                    Permission.SCHOOL_CLASS_AVATAR_READ,
                    Permission.SCHOOL_CLASS_AVATAR_DELETE,

                    Permission.WALLET_INFO_SET,
                    Permission.WALLET_INFO_READ,
                    Permission.WALLET_BALANCE_READ,
                    Permission.WALLET_INFO_CLEAR,

                    Permission.WALLET_HISTORY_READ_ALL,

                    Permission.FUND_CREATE,
                    Permission.FUND_READ,
                    Permission.FUND_UPDATE,
                    Permission.FUND_DELETE,
                    Permission.FUND_CANCEL,
                    Permission.FUND_CREATED_READ_ALL,

                    Permission.FUND_LOGO_READ,
                    Permission.FUND_LOGO_UPDATE,
                    Permission.FUND_LOGO_DELETE,

                    Permission.FUND_MEDIA_FILE_UPLOAD,
                    Permission.FUND_MEDIA_FILE_READ,
                    Permission.FUND_MEDIA_METADATA_READ,
                    Permission.FUND_MEDIA_METADATA_UPDATE,
                    Permission.FUND_MEDIA_FILE_DELETE,

                    Permission.FUND_PAY,
                    Permission.FUND_WITHDRAW,
                    Permission.FUND_DEPOSIT,

                    Permission.FUND_REPORT_GENERATE,
                    Permission.SCHOOL_CLASS_REPORT_GENERATE,
                    Permission.CHILD_REPORT_GENERATE,

                    Permission.FINANCIAL_OPERATION_HISTORY_READ,

                    Permission.PAYMENT_SESSION_CREATE
            )
    ),

    SCHOOL_ADMIN(
            true,
            Set.of(
                    Permission.USER_PASSWORD_CHANGE,

                    Permission.PARENT_READ_ALL,
                    Permission.PARENT_READ,

                    Permission.PARENT_CHILDREN_READ_ALL,
                    Permission.PARENT_SCHOOL_CLASS_READ_ALL,

                    Permission.PARENT_CHILDREN_FUND_READ_ALL,

                    Permission.PARENT_AVATAR_READ,

                    Permission.CHILD_READ,
                    Permission.CHILD_READ_ALL,
                    Permission.CHILD_UPDATE,
                    Permission.CHILD_DELETE,
                    Permission.CHILD_CLASS_JOIN,

                    Permission.CHILD_AVATAR_READ,

                    Permission.SCHOOL_CLASS_CREATE,
                    Permission.SCHOOL_CLASS_READ,
                    Permission.SCHOOL_CLASS_READ_ALL,
                    Permission.SCHOOL_CLASS_UPDATE,
                    Permission.SCHOOL_CLASS_DELETE,

                    Permission.SCHOOL_CLASS_FUND_READ_ALL,
                    Permission.SCHOOL_CLASS_CHILDREN_READ_ALL,

                    Permission.SCHOOL_CLASS_INVITATION_CODE_REGENERATE,

                    Permission.SCHOOL_CLASS_AVATAR_UPDATE,
                    Permission.SCHOOL_CLASS_AVATAR_READ,
                    Permission.SCHOOL_CLASS_AVATAR_DELETE,

                    Permission.FUND_CREATE,
                    Permission.FUND_READ,
                    Permission.FUND_READ_ALL,
                    Permission.FUND_UPDATE,
                    Permission.FUND_DELETE,
                    Permission.FUND_CANCEL,
                    Permission.FUND_CREATED_READ_ALL,

                    Permission.FUND_LOGO_READ,
                    Permission.FUND_LOGO_UPDATE,
                    Permission.FUND_LOGO_DELETE,

                    Permission.FUND_MEDIA_FILE_UPLOAD,
                    Permission.FUND_MEDIA_FILE_READ,
                    Permission.FUND_MEDIA_METADATA_READ,
                    Permission.FUND_MEDIA_METADATA_UPDATE,
                    Permission.FUND_MEDIA_FILE_DELETE,

                    Permission.FUND_WITHDRAW,
                    Permission.FUND_DEPOSIT,

                    Permission.FUND_REPORT_GENERATE,

                    Permission.FINANCIAL_OPERATION_HISTORY_READ
            )
    ),

    SUPER_ADMIN(
            true,
            Set.of(
                    Permission.values()
            )
    );

    private final boolean isAdminRole;
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return authorities;
    }

}
