package org.rogarithm.presize.web;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.rogarithm.presize.service.ImgPolishService;
import org.rogarithm.presize.service.ImgUploadService;
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

    private final ImgPolishService service;
    private final ImgUploadService uploadService;

    public ImgPolishController(ImgPolishService service, ImgUploadService uploadService) {
        this.service = service;
        this.uploadService = uploadService;
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

    @PostMapping(value = "/staging/upscale", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImgUpscaleStagingResponse upscaleImgStaging(
            @RequestParam("taskId") String taskId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("upscaleRatio") String upscaleRatio
    ) throws FileUploadException {

        HealthCheckResponse healthCheckResponse = service.healthCheck();

        if (!healthCheckResponse.isSuccess()) {
            return new ImgUpscaleStagingResponse(500, "ai model has problem", "no url");
        }

        ImgUpscaleStagingRequest request = new ImgUpscaleStagingRequest(taskId, file, upscaleRatio);
        upscaleImgAsync(request);
        return new ImgUpscaleStagingResponse(200, "wait", uploadService.makeUrl(request));
    }

    @Async
    public CompletableFuture<Void> upscaleImgAsync(ImgUpscaleStagingRequest request) throws FileUploadException {
        String upscaledImg = processUpscale(request);
        return uploadService.uploadToS3(request, upscaledImg);
    }

    private String processUpscale(ImgUpscaleStagingRequest request) {
        ImgUpscaleDto dto = ImgUpscaleDto.fromStaging(request);
        ImgUpscaleResponse response = service.uploadImg(dto);
        return response.getResizedImg();
    }
}
