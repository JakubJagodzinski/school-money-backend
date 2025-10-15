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
            Set.of(
                    Permission.CHILD_CREATE,
                    Permission.CHILD_READ,
                    Permission.CHILD_UPDATE,
                    Permission.CHILD_DELETE,

                    Permission.FUND_CREATE,
                    Permission.FUND_READ,
                    Permission.FUND_UPDATE,
                    Permission.FUND_DELETE
            )
    ),

    ADMIN(
            Set.of(
                    Permission.values()
            )
    );

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
