package org.rogarithm.presize.web.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthCheckResponse {
    private final int code;
    private final String message;

    @JsonCreator
    public HealthCheckResponse(
            @JsonProperty("code") int code,
            @JsonProperty("message") String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}
