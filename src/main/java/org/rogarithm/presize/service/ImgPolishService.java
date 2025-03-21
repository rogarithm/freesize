package org.rogarithm.presize.service;

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
    public CompletableFuture<Void> uncropImgAsync(ImgUncropRequest request) {
        CompletableFuture<Void> voidCompletableFuture = processUncrop(request).thenAcceptAsync(result -> {
            uploadService.uploadUncropImgToS3(request, result.getResizedImg());
        });
        return voidCompletableFuture;
    }

    private CompletableFuture<ImgUncropResponse> processUncrop(ImgUncropRequest request) {
        ImgUncropDto dto = ImgUncropDto.from(request);
        return externalApiRequester.uncropImg(dto)
                .toFuture();
    }

    @Async
    public CompletableFuture<Void> upscaleImgAsync(ImgUpscaleRequest request) {
        CompletableFuture<Void> voidCompletableFuture = processUpscale(request).thenAccept(result -> {
            uploadService.uploadUpscaleImgToS3(request, result.getResizedImg());
        });
        return voidCompletableFuture;
    }

    private CompletableFuture<ImgUpscaleResponse> processUpscale(ImgUpscaleRequest request) {
        ImgUpscaleDto dto = ImgUpscaleDto.from(request);
        return externalApiRequester.upscaleImg(dto)
                .toFuture();
    }

    @Async
    public CompletableFuture<Void> squareImgAsync(ImgSquareRequest request) {
        CompletableFuture<Void> voidCompletableFuture = processSquare(request).thenAccept(result -> {
            uploadService.uploadSquareImgToS3(request, result.getResizedImg());
        });
        return voidCompletableFuture;
    }

    private CompletableFuture<ImgSquareResponse> processSquare(ImgSquareRequest request) {
        ImgSquareDto dto = ImgSquareDto.from(request);
        return externalApiRequester.squareImg(dto)
                .toFuture();
    }

    public HealthCheckResponse healthCheck() {
        return externalApiRequester.healthCheck();
    }
}
