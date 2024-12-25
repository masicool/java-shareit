package ru.practicum.shareit.booking.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimeValidatorImpl.class)
public @interface DateTimeValidAnnotation {
    String message() default "Date 'start >= 'end' or date is null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
