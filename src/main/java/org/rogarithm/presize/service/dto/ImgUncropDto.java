package org.rogarithm.presize.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rogarithm.presize.config.ErrorCode;
import org.rogarithm.presize.exception.AiModelRequestMakingFailException;
import org.rogarithm.presize.web.request.ImgUncropRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

public class ImgUncropDto {
    private String img;
    @JsonProperty("target_ratio")
    private String targetRatio;

    public ImgUncropDto(MultipartFile file, String targetRatio) {
        try {
            this.img = Base64.getEncoder().encodeToString(file.getBytes());
        } catch (Exception e) {
            throw new AiModelRequestMakingFailException(ErrorCode.SERVER_FAULT); // "Failed to encode image", e
        }
        this.targetRatio = targetRatio;
    }

    public static ImgUncropDto from(ImgUncropRequest request) {
        return new ImgUncropDto(request.getFile(), request.getTargetRatio());
    }

    public String getImg() {
        return img;
    }

    public String getTargetRatio() {
        return targetRatio;
    }
}
