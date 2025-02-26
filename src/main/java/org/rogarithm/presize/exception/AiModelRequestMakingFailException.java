package org.rogarithm.presize.exception;

import org.rogarithm.presize.config.ErrorCode;

public class AiModelRequestMakingFailException extends RuntimeException {

    private final ErrorCode errorCode;

    public AiModelRequestMakingFailException(ErrorCode errorCode) {
        super(errorCode.getReason());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
