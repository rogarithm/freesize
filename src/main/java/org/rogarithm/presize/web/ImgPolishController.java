package org.rogarithm.presize.web;

import io.awspring.cloud.s3.S3Exception;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.rogarithm.presize.service.ImgPolishService;
import org.rogarithm.presize.service.dto.ImgUncropDto;
import org.rogarithm.presize.service.dto.ImgUpscaleDto;
import org.rogarithm.presize.web.request.ImgUncropRequest;
import org.rogarithm.presize.web.request.ImgUpscaleRequest;
import org.rogarithm.presize.web.request.ImgUpscaleStagingRequest;
import org.rogarithm.presize.web.response.HealthCheckResponse;
import org.rogarithm.presize.web.response.ImgUncropResponse;
import org.rogarithm.presize.web.response.ImgUpscaleResponse;
import org.rogarithm.presize.web.response.ImgUpscaleStagingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@Controller
@ResponseBody
@RequestMapping
public class ImgPolishController {
    private static final Logger log = LoggerFactory.getLogger(ImgPolishController.class);

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final ImgPolishService service;
    private final S3Client s3Client;

    public ImgPolishController(ImgPolishService service, S3Client s3Client) {
        this.service = service;
        this.s3Client = s3Client;
    }

    @PostMapping("/upscale")
    public ImgUpscaleResponse uploadImg(@ModelAttribute ImgUpscaleRequest request) {
        ImgUpscaleDto from = ImgUpscaleDto.from(request);
        return service.uploadImg(from);
    }

    @PostMapping("/uncrop")
    public ImgUncropResponse uncropImg(@ModelAttribute ImgUncropRequest request) {
        ImgUncropDto from = ImgUncropDto.from(request);
        return service.uncropImg(from);
    }

    @PostMapping("/health-check")
    public HealthCheckResponse healthCheck() {
        return service.healthCheck();
    }

    @PostMapping("/staging/upscale")
    public ImgUpscaleStagingResponse upscaleImgStaging(@ModelAttribute ImgUpscaleStagingRequest request) throws FileUploadException {
        log.warn(request.getTaskId());
        log.warn(request.getUpscaleRatio());
        HealthCheckResponse healthCheckResponse = service.healthCheck();

        if (!healthCheckResponse.isSuccess()) {
            return new ImgUpscaleStagingResponse(500, "ai model has problem", "no url");
        }

        upscaleImgAsync(request);

        String fileExtension = ".png";
        String fileName = request.getTaskId() + fileExtension;
        String directoryName = "img/";

        return new ImgUpscaleStagingResponse(200, "wait", "https://" + bucket + "/" + directoryName + fileName);
    }

    @Async
    public CompletableFuture<Void> upscaleImgAsync(ImgUpscaleStagingRequest request) throws FileUploadException {
        ImgUpscaleDto dto = ImgUpscaleDto.fromStaging(request);
        ImgUpscaleResponse response = service.uploadImg(dto);
        String base64EncodedUpscaleImg = response.getResizedImg();

        String fileExtension = ".png";
        String fileName = request.getTaskId() + fileExtension;
        String directoryName = "img/";

        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedUpscaleImg);
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
