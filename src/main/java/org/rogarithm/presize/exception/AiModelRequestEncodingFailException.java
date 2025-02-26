package org.rogarithm.presize.exception;

import org.rogarithm.presize.config.ErrorCode;

public class AiModelRequestEncodingFailException extends RuntimeException {

    private final ErrorCode errorCode;

    public AiModelRequestEncodingFailException(ErrorCode errorCode) {
        super(errorCode.getReason());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
