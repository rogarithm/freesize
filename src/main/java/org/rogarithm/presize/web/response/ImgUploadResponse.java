package org.rogarithm.presize.web.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ImgUploadResponse {
    private final int code;
    @JsonProperty("resized_img")
    private final String resizedImg;
    private final String message;

    @JsonCreator
    public ImgUploadResponse(
            @JsonProperty("code") int code,
            @JsonProperty("resized_img") String resizedImg,
            @JsonProperty("message") String message) {
        this.code = code;
        this.resizedImg = resizedImg;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getResizedImg() {
        return resizedImg;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return code == 200 && resizedImg != null;
    }

    public String getResizedImgAsString() {
        if (resizedImg != null) {
            byte[] decodedBytes = Base64.getDecoder().decode(resizedImg);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        }
        return null;
    }
}
