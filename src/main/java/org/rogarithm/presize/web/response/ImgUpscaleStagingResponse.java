package org.rogarithm.presize.web.response;

public class ImgUpscaleStagingResponse {
    private final String message;
    private final String url;

    public ImgUpscaleStagingResponse(String message, String url) {
        this.message = message;
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }
}
