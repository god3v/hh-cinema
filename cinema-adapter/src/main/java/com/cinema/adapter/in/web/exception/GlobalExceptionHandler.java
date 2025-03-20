package com.cinema.adapter.in.web.exception;

import com.cinema.adapter.in.web.dto.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // API 호출 시 데이터 값이 유효하지 않은 경우
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), "%s (expected: '%s')".formatted(error.getDefaultMessage(), error.getRejectedValue()));
            log.warn("Invalid value for field '{}': '{}' (expected: '{}')", error.getField(), error.getDefaultMessage(), error.getRejectedValue());
        });

        return new ResponseEntity<>(ErrorResponse.of(ErrorType.CLIENT_ERROR.getMessage(messageSource), errors), HttpStatus.BAD_REQUEST);
    }
}
