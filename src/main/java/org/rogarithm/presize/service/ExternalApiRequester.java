package org.rogarithm.presize.service;

import org.rogarithm.presize.config.ErrorCode;
import org.rogarithm.presize.exception.AiModelRequestFailException;
import org.rogarithm.presize.service.dto.ImgSquareDto;
import org.rogarithm.presize.service.dto.ImgUncropDto;
import org.rogarithm.presize.service.dto.ImgUpscaleDto;
import org.rogarithm.presize.web.response.HealthCheckResponse;
import org.rogarithm.presize.web.response.ImgSquareResponse;
import org.rogarithm.presize.web.response.ImgUncropResponse;
import org.rogarithm.presize.web.response.ImgUpscaleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class ExternalApiRequester {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiRequester.class);

    @Value("${ai.model.url.health-check}")
    private String healthCheckUrl;
    @Value("${ai.model.url.upscale}")
    private String upscaleUrl;
    @Value("${ai.model.url.uncrop}")
    private String uncropUrl;
    @Value("${ai.model.url.square}")
    private String squareUrl;
    @Value("${spring.codec.max-in-memory-size}")
    private String maxInMemorySize;

    @Autowired
    private WebClient webClient;

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
            throw new AiModelRequestFailException(ErrorCode.SERVER_FAULT); // "Failed to retrieve a health check response from the AI model"
        }

        if (healthCheckResponse.isSuccess()) {
            return healthCheckResponse;
        }

        throw new AiModelRequestFailException(ErrorCode.SERVER_FAULT); // "Health check error from AI model: " + healthCheckResponse.getMessage()
    }

    public Mono<ImgUpscaleResponse> upscaleImg(ImgUpscaleDto dto) {
        return webClient.post()
                .uri(upscaleUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(ImgUpscaleResponse.class)
                .onErrorMap(WebClientResponseException.class, e -> {
                    log.error("WebClientResponseException: ", e);
                    log.error("Full error cause: ", e.getCause());
                    String errorMsg = e.getCause().getMessage();
                    if (errorMsg.split(":").length >= 2) {
                        String problematicSize = errorMsg.split(":")[1].replaceAll(" ", "");
                        log.error("Current response's buffer size({}) is higher than current max codec size({}).\n" +
                                        "To resolve, edit max codec size higher than {} both in WebClient configuration and application properties!",
                                problematicSize, parseSizeToBytes(maxInMemorySize), problematicSize);
                    }
                    throw new AiModelRequestFailException(ErrorCode.SERVER_FAULT); // "Failed to retrieve a upscale response from the AI model"
                });
    }

    public Mono<ImgUncropResponse> uncropImg(ImgUncropDto dto) {
        return webClient.post()
                .uri(uncropUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(ImgUncropResponse.class)
                .onErrorMap(WebClientResponseException.class, e -> {
                    log.error("WebClientResponseException: ", e);
                    log.error("Full error cause: ", e.getCause());
                    String errorMsg = e.getCause().getMessage();
                    if (errorMsg.split(":").length >= 2) {
                        String problematicSize = errorMsg.split(":")[1].replaceAll(" ", "");
                        log.error("Current response's buffer size({}) is higher than current max codec size({}).\n" +
                                        "To resolve, edit max codec size higher than {} both in WebClient configuration and application properties!",
                                problematicSize, parseSizeToBytes(maxInMemorySize), problematicSize);
                    }
                    throw new AiModelRequestFailException(ErrorCode.SERVER_FAULT); // "Uncrop error from AI model: " + imgUncropResponse.getMessage()
                });
    }

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
            throw new AiModelRequestFailException(ErrorCode.SERVER_FAULT); // "Failed to retrieve a uncrop response from the AI model"
        }

        if (imgSquareResponse.isSuccess()) {
            return imgSquareResponse;
        }

        throw new AiModelRequestFailException(ErrorCode.SERVER_FAULT); // "Uncrop error from AI model: " + imgSquareResponse.getMessage()
    }

    private String parseSizeToBytes(String size) {
        if (size.endsWith("KB")) {
            return Integer.toString(Integer.parseInt(size.replace("KB", "").trim()) * 1024);
        } else if (size.endsWith("MB")) {
            return Integer.toString(Integer.parseInt(size.replace("MB", "").trim()) * 1024 * 1024);
        }
        return size;
    }
}
