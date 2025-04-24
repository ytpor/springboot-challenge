package com.ytpor.api.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private Openapi openapi;

    @Data
    public static class Openapi {
        private String title;
        private String description;
        private String version;
    }
}
