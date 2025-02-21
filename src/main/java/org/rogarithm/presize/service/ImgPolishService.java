package org.rogarithm.presize.service;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.rogarithm.presize.service.dto.ImgSquareDto;
import org.rogarithm.presize.service.dto.ImgUncropDto;
import org.rogarithm.presize.service.dto.ImgUpscaleDto;
import org.rogarithm.presize.web.request.ImgSquareRequest;
import org.rogarithm.presize.web.request.ImgUncropRequest;
import org.rogarithm.presize.web.request.ImgUpscaleRequest;
import org.rogarithm.presize.web.response.HealthCheckResponse;
import org.rogarithm.presize.web.response.ImgSquareResponse;
import org.rogarithm.presize.web.response.ImgUncropResponse;
import org.rogarithm.presize.web.response.ImgUpscaleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ImgPolishService {

    private static final Logger log = LoggerFactory.getLogger(ImgPolishService.class);

    private final ImgUploadService uploadService;
    private final ExternalApiRequester externalApiRequester;

    public ImgPolishService(ImgUploadService uploadService, ExternalApiRequester externalApiRequester) {
        this.uploadService = uploadService;
        this.externalApiRequester = externalApiRequester;
    }

    @Async
    public CompletableFuture<Void> uncropImgAsync(ImgUncropRequest request) throws FileUploadException {
        String upscaledImg = processUncrop(request);
        return uploadService.uploadUncropImgToS3(request, upscaledImg);
    }

    private String processUncrop(ImgUncropRequest request) {
        ImgUncropDto dto = ImgUncropDto.from(request);
        ImgUncropResponse response = externalApiRequester.uncropImg(dto);
        return response.getResizedImg();
    }

    @Async
    public CompletableFuture<Void> upscaleImgAsync(ImgUpscaleRequest request) throws FileUploadException {
        String upscaledImg = null;
        try {
            upscaledImg = processUpscale(request);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return uploadService.uploadUpscaleImgToS3(request, upscaledImg);
    }

    private String processUpscale(ImgUpscaleRequest request) {
        ImgUpscaleDto dto = ImgUpscaleDto.from(request);
        ImgUpscaleResponse response = externalApiRequester.upscaleImg(dto);
        return response.getResizedImg();
    }

    @Async
    public CompletableFuture<Void> squareImgAsync(ImgSquareRequest request) throws FileUploadException {
        String upscaledImg = processSquare(request);
        return uploadService.uploadSquareImgToS3(request, upscaledImg);
    }

    private String processSquare(ImgSquareRequest request) {
        ImgSquareDto dto = ImgSquareDto.from(request);
        ImgSquareResponse response = externalApiRequester.squareImg(dto);
        return response.getResizedImg();
    }

    public HealthCheckResponse healthCheck() {
        return externalApiRequester.healthCheck();
    }
}
