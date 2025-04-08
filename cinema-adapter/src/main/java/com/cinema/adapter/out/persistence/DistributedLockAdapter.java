package com.cinema.adapter.out.persistence;

import com.cinema.application.port.out.DistributedLock;
import com.cinema.domain.exception.CoreException;
import com.cinema.domain.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockAdapter implements DistributedLock {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;

    /**
     * 락을 획득한 후 안전하게 실행하는 함수형 메서드
     */
    @Override
    public <T> T executeWithLock(String key, long waitTime, long leaseTime, Supplier<T> task) {
        RLock lock = redissonClient.getLock(REDISSON_LOCK_PREFIX + key);

        try {
            if (!lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
                throw new CoreException(ErrorType.LOCK_ACQUISITION_FAILED, "Lock key: " + lock.getName());
            }
            return transactionTemplate.execute(status -> task.get());

        } catch (InterruptedException e) {
            log.warn("Redisson Lock Interrupted key: {}", lock.getName());
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 오류 발생", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
