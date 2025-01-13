package org.rogarithm.presize.service.dto;

import org.rogarithm.presize.web.request.ImgUpscaleRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

public class ImgUpscaleDto {
    private String img;
    private int height;
    private int width;

    public ImgUpscaleDto(MultipartFile file, String height, String width) {
        try {
            this.img = encodeWithPadding(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode image", e);
        }
        this.height = Integer.parseInt(height);
        this.width = Integer.parseInt(width);
    }

    public static ImgUpscaleDto from(ImgUpscaleRequest request) {
        return new ImgUpscaleDto(request.getFile(), request.getHeight(), request.getWidth());
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

    private String encodeWithPadding(String base64) {
        int paddingLength = 4 - (base64.length() % 4);
        if (paddingLength < 4) {
            return base64 + "=".repeat(paddingLength);
        }
        return base64;
    }
}
