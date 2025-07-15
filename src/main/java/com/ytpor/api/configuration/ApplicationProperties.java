package com.ytpor.api.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private JWT jwt;
    private MinIO minio;
    private Openapi openapi;
    private RabbitMQ rabbitmq;
    private WeatherApi weatherapi;

    @Data
    public static class JWT {
        private String key;
    }

    @Data
    public static class MinIO {
        private String url;
        private String accessKey;
        private String secretKey;
        private String bucket;
        private Integer objectTtl;
    }

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

    @Data
    public static class WeatherApi {
        private String key;
    }
}
