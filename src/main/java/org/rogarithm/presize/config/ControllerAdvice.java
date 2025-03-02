package org.rogarithm.presize.config;

import org.rogarithm.presize.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(final RuntimeException exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.BAD_REQUEST);
        return errorResponse.makeResponseEntity();
    }

    @ExceptionHandler(AiModelRequestFailException.class)
    protected ResponseEntity<ErrorResponse> handleAiModelRequestFailException(final AiModelRequestFailException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode());
        return errorResponse.makeResponseEntity();
    }

    @ExceptionHandler(AiModelRequestEncodingFailException.class)
    protected ResponseEntity<ErrorResponse> handleAiModelRequestEncodingFailException(final AiModelRequestEncodingFailException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode());
        return errorResponse.makeResponseEntity();
    }

    @ExceptionHandler(AiModelRequestMakingFailException.class)
    protected ResponseEntity<ErrorResponse> handleAiModelRequestMakingFailException(final AiModelRequestMakingFailException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode());
        return errorResponse.makeResponseEntity();
    }

    @ExceptionHandler(S3FileUploadFailException.class)
    protected ResponseEntity<ErrorResponse> handleS3FileUploadFailException(final S3FileUploadFailException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode());
        return errorResponse.makeResponseEntity();
    }

    @ExceptionHandler(StoreTempFileFailException.class)
    protected ResponseEntity<ErrorResponse> handleStoreTempFileFailException(final StoreTempFileFailException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode());
        return errorResponse.makeResponseEntity();
    }
}
