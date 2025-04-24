package com.ytpor.api.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // Declare the service as final to ensure its immutability
    private final ApplicationProperties applicationProperties;

    // Use constructor-based dependency injection
    public SwaggerConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public OpenAPI publicApi() {
        ApplicationProperties.Openapi openapi = applicationProperties.getOpenapi();

        return new OpenAPI()
                .info(new Info().title(openapi.getTitle()).version(openapi.getVersion()).description(openapi.getDescription()));
    }
}
