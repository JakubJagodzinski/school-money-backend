package com.example.schoolmoney.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ApplicationDataInitializer implements ApplicationRunner {

    private final SuperAdminAccountInitializer superAdminAccountInitializer;

    @Override
    public void run(ApplicationArguments args) {
        superAdminAccountInitializer.initialize();
    }

}
