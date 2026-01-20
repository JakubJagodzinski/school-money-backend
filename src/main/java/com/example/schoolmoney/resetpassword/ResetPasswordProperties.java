package com.example.schoolmoney.resetpassword;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "app.password-reset")
public class ResetPasswordProperties {

    private String allowedRedirectUrls;

    private Set<String> allowedRedirectUrlsSet = Set.of();

    @PostConstruct
    public void init() {
        if (isAllowedRedirectUrlsSpecified()) {
            allowedRedirectUrlsSet = Stream.of(allowedRedirectUrls.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            log.info("Allowed redirect urls: {}", allowedRedirectUrlsSet);
        } else {
            allowedRedirectUrlsSet = Set.of();
            log.warn("No allowed redirect urls specified, all redirect urls will be accepted!");
        }
    }

    public boolean isRedirectUrlAllowed(String redirectUrl) {
        if (allowedRedirectUrls.isEmpty()) {
            return true;
        }

        return allowedRedirectUrlsSet.contains(redirectUrl);
    }

    private boolean isAllowedRedirectUrlsSpecified() {
        return allowedRedirectUrls != null && !allowedRedirectUrls.isBlank();
    }

}
