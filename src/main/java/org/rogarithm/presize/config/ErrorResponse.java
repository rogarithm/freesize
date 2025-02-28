package org.rogarithm.presize.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

class ErrorResponse {
    private static final Logger log = LoggerFactory.getLogger(ErrorResponse.class);

    private final Integer code;
    private final String reason;

    ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.reason = errorCode.getReason();
    }

    public Integer getCode() {
        return this.code;
    }

    public String getReason() {
        return this.reason;
    }

    public HttpStatusCode convertHttpStatusCode() {
        Integer code = this.getCode();
        try {
            return HttpStatusCode.valueOf(code);
        } catch (IllegalArgumentException e) {
            log.error(
                    "Cannot convert code into HttpStatusCode: {}. ErrorCode.code should be between 100 <= code <= 999",
                    this.getCode(),
                    e
            );
            throw new IllegalArgumentException(
                    "Cannot convert code into HttpStatusCode: {}. ErrorCode.code should be between 100 <= code <= 999",
                    e
            );
        }
    }

    public ResponseEntity<ErrorResponse> makeResponseEntity() {
        HttpStatusCode httpStatusCode = this.convertHttpStatusCode();
        return ResponseEntity.status(httpStatusCode).body(this);
    }
}
