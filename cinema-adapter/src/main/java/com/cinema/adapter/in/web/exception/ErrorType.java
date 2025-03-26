package com.cinema.adapter.in.web.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@AllArgsConstructor
public enum ErrorType {
    CLIENT_ERROR("error.client_error");

    private final String messageKey;

    public String getMessage(MessageSource messageSource) {
        return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }
}
