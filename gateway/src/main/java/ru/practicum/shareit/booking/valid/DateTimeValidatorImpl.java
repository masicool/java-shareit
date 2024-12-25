package ru.practicum.shareit.booking.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingNewDto;

import java.time.LocalDateTime;

public class DateTimeValidatorImpl implements ConstraintValidator<DateTimeValidAnnotation, BookingNewDto> {
    @Override
    public void initialize(DateTimeValidAnnotation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BookingNewDto value, ConstraintValidatorContext context) {
        LocalDateTime start = value.getStart();
        LocalDateTime end = value.getEnd();

        if (start == null || end == null) return false;

        return start.isBefore(end);
    }
}
