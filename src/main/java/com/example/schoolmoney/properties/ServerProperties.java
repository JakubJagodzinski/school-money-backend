package com.example.schoolmoney.properties;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Data
@Component
@ConfigurationProperties(prefix = "app.server")
public class ServerProperties {

    @NotBlank
    private String publicAddress;

    @PostConstruct
    public void logPublicAddress() {
        log.info("Server public address: {}", publicAddress);
    }

}
