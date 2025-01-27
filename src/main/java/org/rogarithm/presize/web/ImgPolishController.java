package org.rogarithm.presize.web;

import io.awspring.cloud.s3.S3Exception;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.rogarithm.presize.service.ImgPolishService;
import org.rogarithm.presize.service.dto.ImgUncropDto;
import org.rogarithm.presize.service.dto.ImgUpscaleDto;
import org.rogarithm.presize.web.request.ImgUncropRequest;
import org.rogarithm.presize.web.request.ImgUpscaleRequest;
import org.rogarithm.presize.web.response.HealthCheckResponse;
import org.rogarithm.presize.web.response.ImgUncropResponse;
import org.rogarithm.presize.web.response.ImgUpscaleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

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
    public ImgUpscaleResponse upscaleImg(@ModelAttribute ImgUpscaleRequest request) throws FileUploadException {
        MultipartFile file = request.getFile();
        String fileName = file.getOriginalFilename();
        String directoryName = "img/";

        try (InputStream inputStream = file.getInputStream()) {
            byte[] fileBytes = inputStream.readAllBytes();

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(directoryName + fileName)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                            .build(),
                    RequestBody.fromBytes(fileBytes)
            );

            // 파일 업로드 완료 후, 업로드된 파일의 URL 등을 반환 (예를 들면 S3에 업로드한 파일의 URL을 반환)
            String fileUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(directoryName + fileName)).toExternalForm();
            return new ImgUpscaleResponse(200, fileUrl, "success upload");
        } catch (IOException | S3Exception e) {
            throw new FileUploadException("Error occurred during file upload: " + e.getMessage());
        }

//        HealthCheckResponse healthCheckResponse = service.healthCheck();
//        if (healthCheckResponse.isSuccess()) {
//            return new ImgUpscaleResponse(200, "url", "wait");
//        }
//
//        ImgUpscaleDto from = ImgUpscaleDto.from(request);
//        return service.uploadImg(from);
    }
}
