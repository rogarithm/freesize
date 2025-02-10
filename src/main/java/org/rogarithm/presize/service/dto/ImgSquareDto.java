package org.rogarithm.presize.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rogarithm.presize.web.request.ImgSquareRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

public class ImgSquareDto {
    private String img;
    @JsonProperty("target_res")
    private String targetRes;

    public ImgSquareDto(
            MultipartFile file,
            @JsonProperty("target_res") String targetRes
    ) {
        try {
            this.img = Base64.getEncoder().encodeToString(file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode image", e);
        }
        this.targetRes = targetRes;
    }

    public static ImgSquareDto from(ImgSquareRequest request) {
        return new ImgSquareDto(request.getFile(), request.getTargetRes());
    }

    public String getImg() {
        return img;
    }

    public String getTargetRes() {
        return targetRes;
    }
}
