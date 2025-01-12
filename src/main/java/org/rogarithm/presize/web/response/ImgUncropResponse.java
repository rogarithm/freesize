package org.rogarithm.presize.web.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ImgUncropResponse {
    private final int code;
    @JsonProperty("uncrop_img")
    private final String uncropImg;
    private final String message;

    @JsonCreator
    public ImgUncropResponse(
            @JsonProperty("code") int code,
            @JsonProperty("uncrop_img") String uncropImg,
            @JsonProperty("message") String message) {
        this.code = code;
        this.uncropImg = uncropImg;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getUncropImg() {
        return uncropImg;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return code == 200 && uncropImg != null;
    }

    public String getUncropImgAsString() {
        if (uncropImg != null) {
            byte[] decodedBytes = Base64.getDecoder().decode(uncropImg);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        }
        return null;
    }
}
