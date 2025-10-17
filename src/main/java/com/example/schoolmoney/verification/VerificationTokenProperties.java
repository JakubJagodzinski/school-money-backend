package com.example.schoolmoney.verification;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.verification.token")
public class VerificationTokenProperties {

    private long expiryHours;

    private int tokenLength;

}
