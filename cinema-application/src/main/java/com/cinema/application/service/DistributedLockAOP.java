package com.cinema.application.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLockAOP {

    String key();

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    long waitTime() default 3L;

    long leaseTime() default 3L;
}
