package com.cinema.common.exception;

import lombok.Getter;

@Getter
public class CoreException extends RuntimeException {
    private final ErrorType errorType;
    private final String payload;

    public CoreException(ErrorType errorType, String payload) {
        this.errorType = errorType;
        this.payload = payload;
    }
}
