package com.cinema.adapter.in.web.interceptor;

import com.cinema.application.port.out.LookUpRateLimiterPort;
import com.cinema.domain.exception.CoreException;
import com.cinema.domain.exception.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final LookUpRateLimiterPort lookUpRateLimiterPort;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = request.getRemoteAddr();

        if (lookUpRateLimiterPort.isBlocked(ip)) {
            throw new CoreException(ErrorType.IP_BLOCKED, "요청 IP: " + ip);
        }

        if (!lookUpRateLimiterPort.tryAcquire(ip)) {
            lookUpRateLimiterPort.markBlocked(ip);
            throw new CoreException(ErrorType.IP_BLOCKED, "요청 IP: " + ip);
        }

        return true;
    }
}
