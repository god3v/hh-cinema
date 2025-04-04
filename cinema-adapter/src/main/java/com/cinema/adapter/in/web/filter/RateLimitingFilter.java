package com.cinema.adapter.in.web.filter;

import com.cinema.application.port.out.RateLimiterPort;
import com.cinema.domain.exception.CoreException;
import com.cinema.domain.exception.ErrorType;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter implements Filter {

    private final RateLimiterPort rateLimiterPort;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String ip = httpRequest.getRemoteAddr();

        if (rateLimiterPort.isBlocked(ip)) {
            throw new CoreException(ErrorType.IP_BLOCKED, "요청 IP: " + ip);
        }

        if (!rateLimiterPort.tryAcquire(ip)) {
            rateLimiterPort.markBlocked(ip);
            throw new CoreException(ErrorType.IP_BLOCKED, "요청 IP: " + ip);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
