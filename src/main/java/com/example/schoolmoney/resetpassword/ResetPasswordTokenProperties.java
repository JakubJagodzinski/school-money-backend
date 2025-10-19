package com.example.schoolmoney.resetpassword;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.reset-password.token")
public class ResetPasswordTokenProperties {

    private long expiryHours;

    private int tokenLength;

}
