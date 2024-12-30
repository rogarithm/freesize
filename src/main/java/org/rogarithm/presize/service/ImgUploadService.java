package org.rogarithm.presize.service;

import org.rogarithm.presize.service.dto.ImgUploadDto;
import org.rogarithm.presize.web.response.ImgUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ImgUploadService {

    @Value("${ai.model.url}")
    private String aiModelUrl;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public ImgUploadResponse uploadImg(ImgUploadDto dto) {
        WebClient webClient = webClientBuilder.build();
        WebClient.ResponseSpec retrieve = webClient.post()
                .uri(aiModelUrl)
                .bodyValue(dto)
                .retrieve();
        Mono<ImgUploadResponse> response = retrieve.bodyToMono(ImgUploadResponse.class);
        ImgUploadResponse imgUploadResponse = response.block();

        if (imgUploadResponse == null) {
            throw new RuntimeException("Failed to retrieve a response from the AI model");
        }

        if (imgUploadResponse.isSuccess()) {
            return imgUploadResponse;
        }

        throw new RuntimeException("Error from AI model: " + imgUploadResponse.getMessage());
    }
}
