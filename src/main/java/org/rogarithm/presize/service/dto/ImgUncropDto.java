package org.rogarithm.presize.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rogarithm.presize.config.ErrorCode;
import org.rogarithm.presize.exception.AiModelRequestMakingFailException;
import org.rogarithm.presize.web.request.ImgUncropRequest;

import java.util.Base64;

public class ImgUncropDto {
    private String img;
    @JsonProperty("target_ratio")
    private String targetRatio;

    public ImgUncropDto(byte[] fileBytes, String targetRatio) {
        try {
            this.img = Base64.getEncoder().encodeToString(fileBytes);
        } catch (Exception e) {
            throw new AiModelRequestMakingFailException(ErrorCode.SERVER_FAULT); // "Failed to encode image", e
        }
        this.targetRatio = targetRatio;
    }

    public static ImgUncropDto from(ImgUncropRequest request) {
        return new ImgUncropDto(request.getFileBytes(), request.getTargetRatio());
    }

    public String getImg() {
        return img;
    }

    public String getTargetRatio() {
        return targetRatio;
    }
}
