package com.ytpor.api.configuration;

import io.minio.MinioClient;
import io.minio.MakeBucketArgs;
import io.minio.BucketExistsArgs;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(MinioClient.class)
public class MinioInitializer {

    private final MinioClient minioClient;
    private final ApplicationProperties applicationProperties;

    public MinioInitializer(MinioClient minioClient, ApplicationProperties applicationProperties) {
        this.minioClient = minioClient;
        this.applicationProperties = applicationProperties;
    }

    @PostConstruct
    public void ensureBucketExists() {
        try {
            String bucket = applicationProperties.getMinio().getBucket();
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MinIO bucket", e);
        }
    }
}
