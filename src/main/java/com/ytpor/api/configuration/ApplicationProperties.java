package com.ytpor.api.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private Openapi openapi;
    private RabbitMQ rabbitmq;

    @Data
    public static class Openapi {
        private String title;
        private String description;
        private String version;
    }

    @Data
    public static class RabbitMQ {
        private String queueCategory;
        private String queueCategoryKey;
        private boolean queueCategoryDurable;
        private String queueItemAttribute;
        private String queueItemAttributeKey;
        private boolean queueItemAttributeDurable;
        private String exchangeName;
    }
}
