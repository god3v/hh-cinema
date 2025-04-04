package com.cinema.adapter.in.web.aspect;

import com.cinema.adapter.in.web.annotation.PreventDuplicateReservation;
import com.cinema.application.port.out.ReservationRateLimiterPort;
import com.cinema.domain.exception.CoreException;
import com.cinema.domain.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PreventDuplicateReservationAspect {

    private final ReservationRateLimiterPort rateLimiterPort;

    @Around("@annotation(prevent)")
    public Object checkDuplicateReservation(final ProceedingJoinPoint joinPoint, PreventDuplicateReservation prevent) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        // SpEL로 userId, scheduleId 추출
        Long userId = (Long) CustomSpringELParser.getDynamicValue(parameterNames, args, prevent.userId());
        Long scheduleId = (Long) CustomSpringELParser.getDynamicValue(parameterNames, args, prevent.scheduleId());

        if (rateLimiterPort.isBlocked(userId, scheduleId)) {
            throw new CoreException(ErrorType.RESERVATION_REQUEST_BLOCKED, "요청한 상영 일정 ID: " + scheduleId);
        }

        Object result = joinPoint.proceed(); // 예약 진행

        // 예약 성공 시 처리율 제한 관리
        rateLimiterPort.markReserved(userId, scheduleId);
        return result;
    }
}
