package com.example.schoolmoney.auth.access;

import com.example.schoolmoney.user.Permission;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionCheckAspect {

    private final SecurityUtils securityUtils;

    @Before("@annotation(checkPermission)")
    public void checkPermission(CheckPermission checkPermission) {
        Permission permission = checkPermission.value();

        if (!securityUtils.hasPermission(permission.getPermission())) {
            throw new AccessDeniedException("You do not have permission: " + permission);
        }
    }

}
