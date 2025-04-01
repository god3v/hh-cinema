package com.cinema.application.port.out;

import java.util.function.Supplier;

public interface DistributedLock {
    public <T> T executeWithLock(String key, long waitTime, long leaseTime, Supplier<T> task);
}
