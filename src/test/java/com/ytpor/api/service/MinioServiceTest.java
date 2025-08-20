package com.ytpor.api.service;

import com.ytpor.api.configuration.ApplicationProperties;
import com.ytpor.api.configuration.ApplicationProperties.MinIO;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private MinioClient minioClient;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private MinIO minioProps;

    @InjectMocks
    private MinioService minioService;

    @Test
    void testIsMinioReachable_shouldReturnTrue() {
        assertTrue(minioService.isMinioReachable());
    }

    @Test
    void testIsMinioReachable_shouldReturnFalse() {
        MinioService service = new MinioService(applicationProperties, null);
        assertFalse(service.isMinioReachable());
    }

    @Test
    void testGetBucket_shouldReturnBucketName() {
        when(applicationProperties.getMinio()).thenReturn(minioProps);
        when(minioProps.getBucket()).thenReturn("test-bucket");

        assertEquals("test-bucket", minioService.getBucket());
    }

    @Test
    void testUploadFileAndGetPath_shouldUploadSuccessfully() throws Exception {
        when(applicationProperties.getMinio()).thenReturn(minioProps);
        when(minioProps.getBucket()).thenReturn("test-bucket");
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("content".getBytes()));
        when(multipartFile.getSize()).thenReturn(7L);
        when(multipartFile.getContentType()).thenReturn("text/plain");

        String path = minioService.uploadFileAndGetPath(multipartFile);
        assertNotNull(path);
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testUploadFileAndGetPath_shouldThrowIfMinioClientIsNull() {
        MinioService service = new MinioService(applicationProperties, null);
        assertThrows(IllegalStateException.class, () -> service.uploadFileAndGetPath(multipartFile));
    }

    @Test
    void testGeneratePresignedUrl_shouldReturnUrl() throws Exception {
        when(applicationProperties.getMinio()).thenReturn(minioProps);
        when(minioProps.getObjectTtl()).thenReturn(60);
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("http://presigned.url");

        String url = minioService.generatePresignedUrl("test-bucket", "test-object");
        assertEquals("http://presigned.url", url);
    }

    @Test
    void testHashObjectName_shouldGenerateHashedPath() throws Exception {
        String result = minioService.hashObjectName("example.txt");
        assertTrue(result.matches("\\d{4}/\\d{2}/\\d{2}/[a-f0-9]{16}\\.txt"));
    }
}
