package com.example.schoolmoney.config.cors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    private String allowedOrigins;
    private String allowedMethods;
    private String allowedHeaders;

    public List<String> getAllowedOriginsList() {
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .toList();
    }

    public List<String> getAllowedMethodsList() {
        return Arrays.stream(allowedMethods.split(","))
                .map(String::trim)
                .toList();
    }

    public List<String> getAllowedHeadersList() {
        return Arrays.stream(allowedHeaders.split(","))
                .map(String::trim)
                .toList();
    }

}
