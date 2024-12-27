package org.rogarithm.presize.service.dto;

import org.rogarithm.presize.web.request.ImgUploadRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

public class ImgUploadDto {
    private String img;
    private int height;
    private int width;

    public ImgUploadDto(MultipartFile file, String height, String width) {
        try {
            this.img = Base64.getEncoder().encodeToString(file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode image", e);
        }
        this.height = Integer.parseInt(height);
        this.width = Integer.parseInt(width);
    }

    public static ImgUploadDto from(ImgUploadRequest request) {
        return new ImgUploadDto(request.getFile(), request.getHeight(), request.getWidth());
    }

    public String getImg() {
        return img;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
