package com.example.schoolmoney.verification;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.verification.token")
public class VerificationTokenProperties {

    private long expiryHours;

    private int tokenLength;

}
