package org.rogarithm.presize.web;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.rogarithm.presize.service.ImgPolishService;
import org.rogarithm.presize.service.ImgUploadService;
import org.rogarithm.presize.service.dto.ImgUncropDto;
import org.rogarithm.presize.service.dto.ImgUpscaleDto;
import org.rogarithm.presize.web.request.ImgUncropRequest;
import org.rogarithm.presize.web.request.ImgUpscaleRequest;
import org.rogarithm.presize.web.response.HealthCheckResponse;
import org.rogarithm.presize.web.response.ImgUncropResponse;
import org.rogarithm.presize.web.response.ImgUpscaleResponse;
import org.rogarithm.presize.web.response.PollingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@Controller
@ResponseBody
@RequestMapping
public class ImgPolishController {
    private static final Logger log = LoggerFactory.getLogger(ImgPolishController.class);

    private final ImgPolishService polishService;
    private final ImgUploadService uploadService;

    public ImgPolishController(ImgPolishService polishService, ImgUploadService uploadService) {
        this.polishService = polishService;
        this.uploadService = uploadService;
    }

    @PostMapping("/uncrop")
    public PollingResponse uncropImg(
            @RequestParam("taskId") String taskId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("targetRatio") String targetRatio
    ) throws FileUploadException {
        HealthCheckResponse healthCheckResponse = polishService.healthCheck();

        if (!healthCheckResponse.isSuccess()) {
            return new PollingResponse(500, "ai model has problem", "no url");
        }

        ImgUncropRequest request = new ImgUncropRequest(taskId, file, targetRatio);
        uncropImgAsync(request);

        return new PollingResponse(200, "wait", uploadService.makeUncropUrl(request));
    }

    @Async
    public CompletableFuture<Void> uncropImgAsync(ImgUncropRequest request) throws FileUploadException {
        String upscaledImg = processUncrop(request);
        return uploadService.uploadUncropToS3(request, upscaledImg);
    }

    private String processUncrop(ImgUncropRequest request) {
        ImgUncropDto dto = ImgUncropDto.from(request);
        ImgUncropResponse response = polishService.uncropImg(dto);
        return response.getResizedImg();
    }

    @PostMapping("/health-check")
    public HealthCheckResponse healthCheck() {
        return polishService.healthCheck();
    }

    @PostMapping(value = "/upscale", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PollingResponse upscaleImg(
            @RequestParam("taskId") String taskId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("upscaleRatio") String upscaleRatio
    ) throws FileUploadException {

        HealthCheckResponse healthCheckResponse = polishService.healthCheck();

        if (!healthCheckResponse.isSuccess()) {
            return new PollingResponse(500, "ai model has problem", "no url");
        }

        ImgUpscaleRequest request = new ImgUpscaleRequest(taskId, file, upscaleRatio);
        upscaleImgAsync(request);
        return new PollingResponse(200, "wait", uploadService.makeUrl(request));
    }

    @Async
    public CompletableFuture<Void> upscaleImgAsync(ImgUpscaleRequest request) throws FileUploadException {
        String upscaledImg = processUpscale(request);
        return uploadService.uploadToS3(request, upscaledImg);
    }

    private String processUpscale(ImgUpscaleRequest request) {
        ImgUpscaleDto dto = ImgUpscaleDto.from(request);
        ImgUpscaleResponse response = polishService.upscaleImg(dto);
        return response.getResizedImg();
    }
}
