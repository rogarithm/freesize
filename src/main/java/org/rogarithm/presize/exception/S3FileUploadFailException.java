package org.rogarithm.presize.exception;

import org.rogarithm.presize.config.ErrorCode;

public class S3FileUploadFailException extends RuntimeException {

    private final ErrorCode errorCode;

    public S3FileUploadFailException(ErrorCode errorCode) {
        super(errorCode.getReason());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
