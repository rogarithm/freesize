package org.rogarithm.presize.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rogarithm.presize.config.ErrorCode;
import org.rogarithm.presize.exception.AiModelRequestMakingFailException;
import org.rogarithm.presize.web.request.ImgSquareRequest;

import java.util.Base64;

public class ImgSquareDto {
    private String img;
    @JsonProperty("target_res")
    private String targetRes;

    public ImgSquareDto(
            byte[] fileBytes,
            @JsonProperty("target_res") String targetRes
    ) {
        try {
            this.img = Base64.getEncoder().encodeToString(fileBytes);
        } catch (Exception e) {
            throw new AiModelRequestMakingFailException(ErrorCode.SERVER_FAULT); // "Failed to encode image", e
        }
        this.targetRes = targetRes;
    }

    public static ImgSquareDto from(ImgSquareRequest request) {
        return new ImgSquareDto(request.getFileBytes(), request.getTargetRes());
    }

    public String getImg() {
        return img;
    }

    public String getTargetRes() {
        return targetRes;
    }
}
