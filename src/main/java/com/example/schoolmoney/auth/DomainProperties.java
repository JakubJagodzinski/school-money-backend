package com.example.schoolmoney.auth;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "app")
@Component
public class DomainProperties {

    private String domain;

    @PostConstruct
    public void logDomain() {
        if (isDomainBlank()) {
            log.warn("No authorized email domain specified, all emails will be accepted");
        } else {
            log.info("Authorized email domain: {}", domain);
        }
    }

    public boolean isEmailDomainAuthorized(String email) {
        if (isDomainBlank()) {
            return true;
        }

        return email.toLowerCase().endsWith(domain.toLowerCase());
    }

    private boolean isDomainBlank() {
        return domain == null || domain.isBlank();
    }

}
