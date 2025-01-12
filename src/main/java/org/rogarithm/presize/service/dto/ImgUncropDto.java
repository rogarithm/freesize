package org.rogarithm.presize.service.dto;

import org.rogarithm.presize.web.request.ImgUncropRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

public class ImgUncropDto {
    private String img;

    public ImgUncropDto(MultipartFile file) {
        try {
            this.img = Base64.getEncoder().encodeToString(file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode image", e);
        }
    }

    public static ImgUncropDto from(ImgUncropRequest request) {
        return new ImgUncropDto(request.getFile());
    }

    public String getImg() {
        return img;
    }
}
