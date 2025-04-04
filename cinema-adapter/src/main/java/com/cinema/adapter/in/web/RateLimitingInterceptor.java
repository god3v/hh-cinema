package com.cinema.adapter.in.web;

import com.cinema.application.port.out.RateLimiterPort;
import com.cinema.domain.exception.CoreException;
import com.cinema.domain.exception.ErrorType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final RateLimiterPort rateLimiterPort;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = request.getRemoteAddr();

        if (rateLimiterPort.isBlocked(ip)) {
            throw new CoreException(ErrorType.IP_BLOCKED, "요청 IP: " + ip);
        }

        if (!rateLimiterPort.tryAcquire(ip)) {
            rateLimiterPort.markBlocked(ip);
            throw new CoreException(ErrorType.IP_BLOCKED, "요청 IP: " + ip);
        }

        return true;
    }
}
