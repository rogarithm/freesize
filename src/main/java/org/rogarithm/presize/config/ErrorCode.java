package org.rogarithm.presize.config;

public enum ErrorCode {

    BAD_REQUEST        (400, "잘못된 요청"),
    NOT_EXISTS         (404, "리소스가 존재하지 않음");

    private final Integer code;
    private final String reason;

    ErrorCode(Integer code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getReason() {
        return this.reason;
    }
}
