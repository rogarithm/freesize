package org.rogarithm.presize.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rogarithm.presize.web.request.ImgUpscaleRequest;
import org.rogarithm.presize.web.request.ImgUpscaleStagingRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

public class ImgUpscaleDto {
    private String img;
    @JsonProperty("upscale_ratio")
    private String upscaleRatio;

    public ImgUpscaleDto(
            MultipartFile file,
            @JsonProperty("upscale_ratio") String upscaleRatio
    ) {
        try {
            this.img = encodeWithPadding(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode image", e);
        }
        this.upscaleRatio = upscaleRatio;
    }

    public static ImgUpscaleDto from(ImgUpscaleRequest request) {
        return new ImgUpscaleDto(request.getFile(), request.getUpscaleRatio());
    }

    public static ImgUpscaleDto fromStaging(ImgUpscaleStagingRequest request) {
        return new ImgUpscaleDto(request.getFile(), request.getUpscaleRatio());
    }

    public String getImg() {
        return img;
    }

    public String getUpscaleRatio() {
        return upscaleRatio;
    }

    private String encodeWithPadding(String base64) {
        int paddingLength = 4 - (base64.length() % 4);
        if (paddingLength < 4) {
            return base64 + "=".repeat(paddingLength);
        }
        return base64;
    }
}
