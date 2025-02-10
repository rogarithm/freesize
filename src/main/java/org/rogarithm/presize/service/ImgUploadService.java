package org.rogarithm.presize.service;

import io.awspring.cloud.s3.S3Exception;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.rogarithm.presize.web.request.ImgUncropRequest;
import org.rogarithm.presize.web.request.ImgUpscaleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@Service
public class ImgUploadService {

    private static final Logger log = LoggerFactory.getLogger(ImgUploadService.class);

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region}")
    private String region;

    private final S3Client s3Client;

    public ImgUploadService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public CompletableFuture<Void> uploadToS3(ImgUpscaleRequest request,
                                              String polishedImg
    ) throws FileUploadException {
        String fileExtension = ".png";
        String fileName = request.getTaskId() + fileExtension;
        String directoryName = "img/";

        byte[] decodedBytes = Base64.getDecoder().decode(polishedImg);
        try (InputStream inputStream = new ByteArrayInputStream(decodedBytes)) {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(directoryName + fileName)
                            .contentType("image/png")
                            .contentLength(Long.valueOf(decodedBytes.length))
                            .build(),
                    RequestBody.fromInputStream(inputStream, decodedBytes.length)
            );
            return new CompletableFuture<>();
        } catch (IOException | S3Exception e) {
            throw new FileUploadException("Error occurred during file upload: " + e.getMessage());
        }
    }

    public String makeUrl(String taskId) {
        String fileExtension = ".png";
        String fileName = taskId + fileExtension;
        String directoryName = "img";
        return "https://" + bucket + ".s3." + region + ".amazonaws.com" + "/" + directoryName + "/" + fileName;
    }

    public CompletableFuture<Void> uploadUncropToS3(ImgUncropRequest request,
                                              String polishedImg
    ) throws FileUploadException {
        String fileExtension = ".png";
        String fileName = request.getTaskId() + fileExtension;
        String directoryName = "img/";

        byte[] decodedBytes = Base64.getDecoder().decode(polishedImg);
        try (InputStream inputStream = new ByteArrayInputStream(decodedBytes)) {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(directoryName + fileName)
                            .contentType("image/png")
                            .contentLength(Long.valueOf(decodedBytes.length))
                            .build(),
                    RequestBody.fromInputStream(inputStream, decodedBytes.length)
            );
            return new CompletableFuture<>();
        } catch (IOException | S3Exception e) {
            throw new FileUploadException("Error occurred during file upload: " + e.getMessage());
        }
    }
}
