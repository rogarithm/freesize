package org.rogarithm.presize.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rogarithm.presize.config.ErrorCode;
import org.rogarithm.presize.exception.AiModelRequestMakingFailException;
import org.rogarithm.presize.service.TempFileStoreManager;
import org.rogarithm.presize.web.request.ImgUpscaleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

public class ImgUpscaleDto {
    private static final Logger log = LoggerFactory.getLogger(ImgUpscaleDto.class);

    private String img;
    @JsonProperty("upscale_ratio")
    private String upscaleRatio;

    public ImgUpscaleDto(
            MultipartFile file,
            @JsonProperty("upscale_ratio") String upscaleRatio
    ) {
        try {
            byte[] fileBytes = file.getBytes();

            TempFileStoreManager tempFileStoreManager = new TempFileStoreManager();
            tempFileStoreManager.store(fileBytes, file.getOriginalFilename());

            // Base64 인코딩
            this.img = encodeWithPadding(Base64.getEncoder().encodeToString(fileBytes));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AiModelRequestMakingFailException(ErrorCode.SERVER_FAULT); // "ImgUpscaleDto: Failed to encode image", e
        }
        this.upscaleRatio = upscaleRatio;
    }

    public static ImgUpscaleDto from(ImgUpscaleRequest request) {
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
