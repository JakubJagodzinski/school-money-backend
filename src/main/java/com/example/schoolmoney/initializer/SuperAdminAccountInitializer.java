package com.example.schoolmoney.initializer;

import com.example.schoolmoney.user.Role;
import com.example.schoolmoney.user.User;
import com.example.schoolmoney.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class SuperAdminAccountInitializer {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SuperAdminAccountConfig superAdminAccountConfig;

    private static final String SUPER_ADMIN_FIRST_NAME = "Super";
    private static final String SUPER_ADMIN_LAST_NAME = "Admin";

    public void initialize() {
        if (!userRepository.existsByEmail(superAdminAccountConfig.getEmail())) {
            User admin = new User();

            admin.setEmail(superAdminAccountConfig.getEmail());
            admin.setFirstName(SUPER_ADMIN_FIRST_NAME);
            admin.setLastName(SUPER_ADMIN_LAST_NAME);
            admin.setPassword(passwordEncoder.encode(superAdminAccountConfig.getPassword()));
            admin.setRole(Role.SUPER_ADMIN);
            admin.setVerified(true);

            userRepository.save(admin);
        }
    }

}
