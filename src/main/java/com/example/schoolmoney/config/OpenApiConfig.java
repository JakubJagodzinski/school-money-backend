package com.example.schoolmoney.config;

import com.example.schoolmoney.common.constants.ApiConstants;
import com.example.schoolmoney.common.constants.SecurityConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("/");

        return new OpenAPI()
                .info(
                        new Info()
                                .title(ApiConstants.API_INFO_TITLE)
                                .version(ApiConstants.API_INFO_VERSION)
                                .description(ApiConstants.API_INFO_DESCRIPTION)
                )
                .servers(List.of(server))
                .addSecurityItem(new SecurityRequirement().addList(SecurityConstants.SECURITY_SCHEME_NAME))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SecurityConstants.SECURITY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme(SecurityConstants.BEARER_SCHEME)
                                                .bearerFormat(SecurityConstants.BEARER_FORMAT_JWT)
                                )
                );
    }

    @Bean
    public GroupedOpenApi appApi() {
        return GroupedOpenApi.builder()
                .group("App")
                .pathsToMatch("/api/v1/app/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("Admin")
                .pathsToMatch("/api/v1/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi superAdminApi() {
        return GroupedOpenApi.builder()
                .group("Super Admin")
                .pathsToMatch("/api/v1/super-admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("Auth")
                .pathsToMatch("/api/v1/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi usersApi() {
        return GroupedOpenApi.builder()
                .group("Users")
                .pathsToMatch("/api/v1/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi parentsApi() {
        return GroupedOpenApi.builder()
                .group("Parents")
                .pathsToMatch("/api/v1/parents/**")
                .build();
    }

    @Bean
    public GroupedOpenApi childrenApi() {
        return GroupedOpenApi.builder()
                .group("Children")
                .pathsToMatch("/api/v1/children/**")
                .build();
    }

    @Bean
    public GroupedOpenApi schoolClassesApi() {
        return GroupedOpenApi.builder()
                .group("School Classes")
                .pathsToMatch("/api/v1/school-classes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi fundsApi() {
        return GroupedOpenApi.builder()
                .group("Funds")
                .pathsToMatch("/api/v1/funds/**")
                .build();
    }

    @Bean
    public GroupedOpenApi walletsApi() {
        return GroupedOpenApi.builder()
                .group("Wallets")
                .pathsToMatch("/api/v1/wallets/**")
                .build();
    }

    @Bean
    public GroupedOpenApi paymentsApi() {
        return GroupedOpenApi.builder()
                .group("Payments")
                .pathsToMatch("/api/v1/payments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi payoutsApi() {
        return GroupedOpenApi.builder()
                .group("Payouts")
                .pathsToMatch("/api/v1/payouts/**")
                .build();
    }

    @Bean
    public GroupedOpenApi jokesApi() {
        return GroupedOpenApi.builder()
                .group("Jokes")
                .pathsToMatch("/api/v1/jokes/**")
                .build();
    }

}
