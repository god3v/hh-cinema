package com.cinema.adapter.in.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValue {

    // 유효하지 않은 값일 경우 나오는 메시지
    String message() default "Invalid value";

    // 검사 대상의 enum 클래스 지정
    Class<?>[] groups() default {};

    // 페이로드
    Class<? extends Payload>[] payload() default {};

    // 유효성 검사할 Enum 클래스 지정
    Class<? extends Enum<?>> enumClass();
}