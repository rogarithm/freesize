package org.rogarithm.presize.config;

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
}
