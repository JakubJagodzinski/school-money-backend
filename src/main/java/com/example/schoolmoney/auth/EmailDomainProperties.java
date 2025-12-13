package com.example.schoolmoney.auth;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "app")
@Component
public class EmailDomainProperties {

    private String allowedEmailDomains;

    private Set<String> allowedEmailDomainsSet = Set.of();

    @PostConstruct
    public void init() {
        if (isAllowedEmailDomainsSpecified()) {
            allowedEmailDomainsSet = Stream.of(allowedEmailDomains.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            log.info("Allowed email domains: {}", allowedEmailDomainsSet);
        } else {
            allowedEmailDomainsSet = Set.of();
            log.warn("No allowed email domains specified, all emails will be accepted");
        }
    }

    private String extractDomainFromEmail(String email) {
        return email.substring(email.indexOf('@') + 1).toLowerCase();
    }

    public boolean isEmailDomainAllowed(String email) {
        if (allowedEmailDomainsSet.isEmpty()) {
            return true;
        }

        return allowedEmailDomainsSet.contains(extractDomainFromEmail(email));
    }

    private boolean isAllowedEmailDomainsSpecified() {
        return allowedEmailDomains != null && !allowedEmailDomains.isBlank();
    }

}
