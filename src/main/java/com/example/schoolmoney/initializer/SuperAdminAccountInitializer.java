package com.example.schoolmoney.initializer;

import com.example.schoolmoney.user.Role;
import com.example.schoolmoney.user.User;
import com.example.schoolmoney.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SuperAdminAccountInitializer {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SuperAdminAccountConfig superAdminAccountConfig;

    public void initialize() {
        if (!userRepository.existsByEmail(superAdminAccountConfig.getEmail())) {
            createSuperAdminAccount();

            log.info("Superadmin account created: {}", superAdminAccountConfig.getEmail());
        }
    }

    private void createSuperAdminAccount() {
        User admin = User.builder()
                .firstName(superAdminAccountConfig.getFirstName())
                .lastName(superAdminAccountConfig.getLastName())
                .email(superAdminAccountConfig.getEmail())
                .password(passwordEncoder.encode(superAdminAccountConfig.getPassword()))
                .role(Role.SUPER_ADMIN)
                .isVerified(true)
                .build();

        userRepository.save(admin);
    }

}
