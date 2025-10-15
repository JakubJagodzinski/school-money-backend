package com.example.schoolmoney.config;

import com.example.schoolmoney.common.constants.ApiConstants;
import com.example.schoolmoney.common.constants.SecurityConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title(ApiConstants.API_INFO_TITLE)
                                .version(ApiConstants.API_INFO_VERSION)
                                .description(ApiConstants.API_INFO_DESCRIPTION)
                )
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

}
