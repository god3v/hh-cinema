package com.cinema.adapter.in.web.exception;

import com.cinema.adapter.in.web.dto.response.ErrorResponse;
import com.cinema.domain.exception.CoreException;
import com.cinema.domain.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ErrorResponse> handleCoreException(CoreException e) {
        ErrorType errorType = e.getErrorType();
        String message = errorType.getMessage(messageSource);
        String payload = e.getPayload();

        logMessageBasedOnLevel(errorType.getLogLevel(), message, payload);

        HttpStatus status = getHttpStatusForErrorCode(errorType);

        return new ResponseEntity<>(ErrorResponse.of(message, e.getPayload()), status);
    }

    // API 호출 시 데이터 값이 유효하지 않은 경우
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), "%s (expected: '%s')".formatted(error.getDefaultMessage(), error.getRejectedValue()));
            log.warn("Invalid value for field '{}': '{}' (expected: '{}')", error.getField(), error.getDefaultMessage(), error.getRejectedValue());
        });

        return new ResponseEntity<>(ErrorResponse.of(ErrorType.HTTP_MESSAGE_NOT_READABLE.getMessage(messageSource), errors), HttpStatus.BAD_REQUEST);
    }

    private void logMessageBasedOnLevel(LogLevel logLevel, String message, String payload) {
        switch (logLevel) {
            case ERROR -> log.error("Business ERROR Occurred: {}, {}", message, payload);
            case WARN -> log.warn("Business WARN Occurred: {}, {}", message, payload);
            default -> log.info("Business INFO Occurred: {}, {}", message, payload);
        }
    }

    private HttpStatus getHttpStatusForErrorCode(ErrorType errorType) {
        switch (errorType.getErrorCode()) {
            case DB_ERROR -> {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
            case CLIENT_ERROR -> {
                return HttpStatus.BAD_REQUEST;
            }
            default -> {
                return HttpStatus.OK;
            }
        }
    }
}
