package dev.emrullah.ai_panel.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    @Value("${minio.bucket}")
    private String bucket;

    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String upload(byte[] data,
                         String fileName,
                         String contentType) throws Exception {

        String objectName = UUID.randomUUID() + "_" + fileName;

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(
                                new ByteArrayInputStream(data),
                                (long) data.length,
                                -1L)
                        .contentType(contentType)
                        .build());

        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Http.Method.GET)
                        .bucket(bucket)
                        .object(objectName)
                        .expiry(1, TimeUnit.HOURS)
                        .build());
    }
}
