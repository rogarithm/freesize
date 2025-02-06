package org.rogarithm.presize.web.response;

public class PollingResponse {
    private final int code;
    private final String message;
    private final String url;

    public PollingResponse(int code, String message, String url) {
        this.code = code;
        this.message = message;
        this.url = url;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }
}
