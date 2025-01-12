package org.rogarithm.presize.service;

import org.rogarithm.presize.service.dto.ImgUncropDto;
import org.rogarithm.presize.service.dto.ImgUploadDto;
import org.rogarithm.presize.web.response.ImgUncropResponse;
import org.rogarithm.presize.web.response.ImgUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class ImgUploadService {

    private static final Logger log = LoggerFactory.getLogger(ImgUploadService.class);

    @Value("${ai.model.url.upscale}")
    private String upscaleUrl;
    @Value("${ai.model.url.uncrop}")
    private String uncropUrl;

    @Value("${spring.codec.max-in-memory-size}")
    private String maxInMemorySize;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public ImgUploadResponse uploadImg(ImgUploadDto dto) {
        WebClient webClient = webClientBuilder
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(codecs -> codecs
                                .defaultCodecs()
                                .maxInMemorySize(20 * 1024 * 1024))
                        .build()).
                build();

        WebClient.ResponseSpec retrieve = webClient.post()
                .uri(upscaleUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve();

        Mono<ImgUploadResponse> response = retrieve.bodyToMono(ImgUploadResponse.class);

        ImgUploadResponse imgUploadResponse = null;

        try {
            log.debug("Attempting to block response...");
            imgUploadResponse = response.block();
            log.debug("Successfully retrieved response");
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

        if (imgUploadResponse == null) {
            throw new RuntimeException("Failed to retrieve a response from the AI model");
        }

        if (imgUploadResponse.isSuccess()) {
            return imgUploadResponse;
        }

        throw new RuntimeException("Error from AI model: " + imgUploadResponse.getMessage());
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
    public ImgUncropResponse uncropImg(ImgUncropDto dto) {
        WebClient webClient = webClientBuilder
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(codecs -> codecs
                                .defaultCodecs()
                                .maxInMemorySize(20 * 1024 * 1024))
                        .build()).
                build();

        WebClient.ResponseSpec retrieve = webClient.post()
                .uri(uncropUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve();

        Mono<ImgUncropResponse> response = retrieve.bodyToMono(ImgUncropResponse.class);

        ImgUncropResponse imgUncropResponse = null;

        try {
            log.debug("Attempting to block response...");
            imgUncropResponse = response.block();
            log.debug("Successfully retrieved response");
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
            throw new RuntimeException("Failed to retrieve a response from the AI model");
        }

        if (imgUncropResponse.isSuccess()) {
            return imgUncropResponse;
        }

        throw new RuntimeException("Error from AI model: " + imgUncropResponse.getMessage());
    }
}
