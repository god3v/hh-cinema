package com.cinema.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@AllArgsConstructor
public enum ErrorType {
    RESOURCE_NOT_FOUND(ErrorCode.NOT_FOUND, "exception.entity.not_found", LogLevel.WARN),
    HTTP_MESSAGE_NOT_READABLE(ErrorCode.CLIENT_ERROR, "exception.client_error", LogLevel.WARN),
    INVALID_SEAT_SELECTION(ErrorCode.CLIENT_ERROR, "exception.seat.invalid.selection", LogLevel.WARN),
    SEAT_RESERVATION_EXCEEDED(ErrorCode.CLIENT_ERROR, "exception.seat.reservation_exceeded", LogLevel.WARN),
    SEATS_NOT_CONTINUOUS(ErrorCode.CLIENT_ERROR, "exception.seat.not_continuous", LogLevel.WARN),
    EVENT_PUBLISH_FAILED(ErrorCode.SERVER_ERROR, "exception.event.publish_failed", LogLevel.WARN),
    LOCK_ACQUISITION_FAILED(ErrorCode.CONFLICT, "exception.lock.acquisition_failed", LogLevel.WARN);

    private final ErrorCode errorCode;
    private final String messageKey;
    private final LogLevel logLevel;

    public String getMessage(MessageSource messageSource) {
        return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }
}
