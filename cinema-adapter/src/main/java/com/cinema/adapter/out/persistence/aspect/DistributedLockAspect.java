package com.cinema.adapter.out.persistence.aspect;

import com.cinema.adapter.in.web.aspect.CustomSpringELParser;
import com.cinema.application.service.DistributedLockAOP;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(distributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint, DistributedLockAOP distributedLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
        RLock rLock = redissonClient.getLock(key);

        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!available) {
                return false;
            }
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            log.warn("Redisson Lock Interrupted serviceName: {}, key: {}", method.getName(), key);
            throw new InterruptedException();
        } finally {
            try {
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.warn("Redisson Lock Already UnLock serviceName: {}, key: {}", method.getName(), key);
            }
        }
    }
}
