package com.cinema.adapter.in.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<EnumValue, CharSequence> {

    private Enum<?>[] enumConstants;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumConstants = constraintAnnotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        // enum 값과 일치하는지 확인
        for (Enum<?> enumConstant : enumConstants) {
            if (enumConstant.name().equalsIgnoreCase(value.toString())) {
                return true;
            }
        }
        return false;
    }
}
