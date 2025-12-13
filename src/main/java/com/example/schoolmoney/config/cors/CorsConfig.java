package com.example.schoolmoney.config.cors;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CorsConfig {

    private final CorsProperties corsProperties;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(corsProperties.getAllowedOriginsList());
        config.setAllowedMethods(corsProperties.getAllowedMethodsList());
        config.setAllowedHeaders(corsProperties.getAllowedHeadersList());
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public Filter logIncomingRequestsFilter() {
        return (request, response, chain) -> {
            HttpServletRequest req = (HttpServletRequest) request;

            String method = req.getMethod();
            String path = req.getRequestURI();
            String origin = req.getHeader("Origin");
            String ip = req.getRemoteAddr();

            log.info("Incoming request: method={}, path={}, origin={}, ip={}", method, path, origin, ip);

            chain.doFilter(request, response);
        };
    }

}
