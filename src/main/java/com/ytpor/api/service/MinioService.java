package com.ytpor.api.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ytpor.api.configuration.ApplicationProperties;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;

@Service
public class MinioService {

    private final ApplicationProperties applicationProperties;
    private final MinioClient minioClient;

    public MinioService(
            ApplicationProperties applicationProperties,
            MinioClient minioClient) {
        this.applicationProperties = applicationProperties;
        this.minioClient = minioClient;
    }

    public String uploadFileAndGetPath(MultipartFile file) throws IOException {
        validateFile(file);

        String objectName;
        try {
            objectName = hashObjectName(file.getOriginalFilename());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash object name", e);
        }

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(applicationProperties.getMinio().getBucket())
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (Exception e) {
            throw new IOException("Failed to upload file to MinIO", e);
        }

        return objectName;
    }

    private void validateFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("File name must not be null or empty");
        }
    }

    public String hashObjectName(String filename) throws NoSuchAlgorithmException {
        int dotIndex = filename.lastIndexOf('.');
        String baseName = (dotIndex != -1) ? filename.substring(0, dotIndex) : filename;
        String extension = (dotIndex != -1) ? filename.substring(dotIndex) : "";

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String combined = baseName + timestamp;

        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hashBytes = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
        StringBuilder hashString = new StringBuilder();
        for (byte b : hashBytes) {
            hashString.append(String.format("%02x", b));
        }

        String shortHash = hashString.toString().substring(0, 16);
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        return datePrefix + "/" + shortHash + extension;
    }

    public String generatePresignedUrl(String bucket, String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucket)
                        .object(objectName)
                        .expiry(applicationProperties.getMinio().getObjectTtl())
                        .build());

    }

    public String getBucket() {
        return applicationProperties.getMinio().getBucket();
    }
}
