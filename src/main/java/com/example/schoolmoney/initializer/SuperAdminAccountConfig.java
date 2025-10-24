package com.example.schoolmoney.initializer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.superadmin")
public class SuperAdminAccountConfig {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

}
