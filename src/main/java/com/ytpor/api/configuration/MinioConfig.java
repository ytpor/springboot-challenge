package com.ytpor.api.configuration;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    private final ApplicationProperties applicationProperties;

    public MinioConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public MinioClient minioClient() {
        ApplicationProperties.MinIO minio = applicationProperties.getMinio();
        return MinioClient.builder()
            .endpoint(minio.getUrl())
            .credentials(minio.getAccessKey(), minio.getSecretKey())
            .build();
    }
}
