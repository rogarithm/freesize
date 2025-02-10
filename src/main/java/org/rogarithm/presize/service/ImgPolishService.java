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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Service
public class ImgPolishService {

    private static final Logger log = LoggerFactory.getLogger(ImgPolishService.class);

    @Value("${ai.model.url.upscale}")
    private String upscaleUrl;
    @Value("${ai.model.url.uncrop}")
    private String uncropUrl;
    @Value("${ai.model.url.squareUrl}")
    private String squareUrl;
    @Value("${ai.model.url.health-check}")
    private String healthCheckUrl;
    @Value("${spring.codec.max-in-memory-size}")
    private String maxInMemorySize;

    @Autowired
    private WebClient webClient;

    private final ImgUploadService uploadService;

    public ImgPolishService(ImgUploadService uploadService) {
        this.uploadService = uploadService;
    }

    @Async
    public CompletableFuture<Void> uncropImgAsync(ImgUncropRequest request) throws FileUploadException {
        String upscaledImg = processUncrop(request);
        return uploadService.uploadUncropImgToS3(request, upscaledImg);
    }

    private String processUncrop(ImgUncropRequest request) {
        ImgUncropDto dto = ImgUncropDto.from(request);
        ImgUncropResponse response = uncropImg(dto);
        return response.getResizedImg();
    }

    @Async
    public CompletableFuture<Void> upscaleImgAsync(ImgUpscaleRequest request) throws FileUploadException {
        String upscaledImg = processUpscale(request);
        return uploadService.uploadUpscaleImgToS3(request, upscaledImg);
    }

    private String processUpscale(ImgUpscaleRequest request) {
        ImgUpscaleDto dto = ImgUpscaleDto.from(request);
        ImgUpscaleResponse response = upscaleImg(dto);
        return response.getResizedImg();
    }

    @Async
    public CompletableFuture<Void> squareImgAsync(ImgSquareRequest request) throws FileUploadException {
        String upscaledImg = processSquare(request);
        return uploadService.uploadSquareImgToS3(request, upscaledImg);
    }

    private String processSquare(ImgSquareRequest request) {
        ImgSquareDto dto = ImgSquareDto.from(request);
        ImgSquareResponse response = squareImg(dto);
        return response.getResizedImg();
    }

    @Transactional
    public ImgUpscaleResponse upscaleImg(ImgUpscaleDto dto) {
        WebClient.ResponseSpec retrieve = webClient.post()
                .uri(upscaleUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve();

        Mono<ImgUpscaleResponse> response = retrieve.bodyToMono(ImgUpscaleResponse.class);

        ImgUpscaleResponse imgUpscaleResponse = null;

        try {
            imgUpscaleResponse = response.block();
        } catch (WebClientResponseException e) {
            log.error("WebClientResponseException: ", e);
            log.error("Full error cause: ", e.getCause());
            String errorMsg = e.getCause().getMessage();
            if (errorMsg.split(":").length >= 2) {
                String problematicSize = errorMsg.split(":")[1].replaceAll(" ", "");
                log.error("Current response's buffer size({}) is higher than current max codec size({}).\n" +
                                "To resolve, edit max codec size higher than {} both in WebClient configuration and application properties!",
                        problematicSize, parseSizeToBytes(maxInMemorySize), problematicSize);
            }
        }

        if (imgUpscaleResponse == null) {
            throw new RuntimeException("Failed to retrieve a upscale response from the AI model");
        }

        if (imgUpscaleResponse.isSuccess()) {
            return imgUpscaleResponse;
        }

        throw new RuntimeException("Upscale error from AI model: " + imgUpscaleResponse.getMessage());
    }

    @Transactional
    public ImgUncropResponse uncropImg(ImgUncropDto dto) {
        WebClient.ResponseSpec retrieve = webClient.post()
                .uri(uncropUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve();

        Mono<ImgUncropResponse> response = retrieve.bodyToMono(ImgUncropResponse.class);

        ImgUncropResponse imgUncropResponse = null;

        try {
            imgUncropResponse = response.block();
        } catch (WebClientResponseException e) {
            log.error("WebClientResponseException: ", e);
            log.error("Full error cause: ", e.getCause());
            String errorMsg = e.getCause().getMessage();
            if (errorMsg.split(":").length >= 2) {
                String problematicSize = errorMsg.split(":")[1].replaceAll(" ", "");
                log.error("Current response's buffer size({}) is higher than current max codec size({}).\n" +
                                "To resolve, edit max codec size higher than {} both in WebClient configuration and application properties!",
                        problematicSize, parseSizeToBytes(maxInMemorySize), problematicSize);
            }
        }

        if (imgUncropResponse == null) {
            throw new RuntimeException("Failed to retrieve a uncrop response from the AI model");
        }

        if (imgUncropResponse.isSuccess()) {
            return imgUncropResponse;
        }

        throw new RuntimeException("Uncrop error from AI model: " + imgUncropResponse.getMessage());
    }

    @Transactional
    public ImgSquareResponse squareImg(ImgSquareDto dto) {
        WebClient.ResponseSpec retrieve = webClient.post()
                .uri(squareUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve();

        Mono<ImgSquareResponse> response = retrieve.bodyToMono(ImgSquareResponse.class);

        ImgSquareResponse imgSquareResponse = null;

        try {
            imgSquareResponse = response.block();
        } catch (WebClientResponseException e) {
            log.error("WebClientResponseException: ", e);
            log.error("Full error cause: ", e.getCause());
            String errorMsg = e.getCause().getMessage();
            if (errorMsg.split(":").length >= 2) {
                String problematicSize = errorMsg.split(":")[1].replaceAll(" ", "");
                log.error("Current response's buffer size({}) is higher than current max codec size({}).\n" +
                                "To resolve, edit max codec size higher than {} both in WebClient configuration and application properties!",
                        problematicSize, parseSizeToBytes(maxInMemorySize), problematicSize);
            }
        }

        if (imgSquareResponse == null) {
            throw new RuntimeException("Failed to retrieve a uncrop response from the AI model");
        }

        if (imgSquareResponse.isSuccess()) {
            return imgSquareResponse;
        }

        throw new RuntimeException("Uncrop error from AI model: " + imgSquareResponse.getMessage());
    }

    private String parseSizeToBytes(String size) {
        if (size.endsWith("KB")) {
            return Integer.toString(Integer.parseInt(size.replace("KB", "").trim()) * 1024);
        } else if (size.endsWith("MB")) {
            return Integer.toString(Integer.parseInt(size.replace("MB", "").trim()) * 1024 * 1024);
        }
        return size;
    }

    @Transactional
    public HealthCheckResponse healthCheck() {
        WebClient.ResponseSpec retrieve = webClient.post()
                .uri(healthCheckUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();

        Mono<HealthCheckResponse> response = retrieve.bodyToMono(HealthCheckResponse.class);

        HealthCheckResponse healthCheckResponse = null;

        try {
            healthCheckResponse = response.block();
        } catch (WebClientResponseException e) {
            log.error("WebClientResponseException: ", e);
            log.error("Full error cause: ", e.getCause());
        }

        if (healthCheckResponse == null) {
            throw new RuntimeException("Failed to retrieve a health check response from the AI model");
        }

        if (healthCheckResponse.isSuccess()) {
            return healthCheckResponse;
        }

        throw new RuntimeException("Health check error from AI model: " + healthCheckResponse.getMessage());
    }
}
