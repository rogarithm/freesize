package org.rogarithm.presize.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ImgUploadResponse {
    @JsonProperty("resized_img")
    private String resizedImg;

    public ImgUploadResponse(@JsonProperty("resized_img") String resizedImg) {
        this.resizedImg = resizedImg;
    }

    public String getResizedImg() {
        return resizedImg;
    }

    public String getResizedImgAsString() {
        if (resizedImg != null) {
            byte[] decodedBytes = Base64.getDecoder().decode(resizedImg);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        }
        return null;
    }
}
