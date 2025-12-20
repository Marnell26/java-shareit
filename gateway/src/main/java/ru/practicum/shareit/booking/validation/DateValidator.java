package ru.practicum.shareit.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

public class DateValidator implements ConstraintValidator<StartBeforeEnd, CreateBookingDto> {
    @Override
    public boolean isValid(CreateBookingDto dto, ConstraintValidatorContext context) {
        if (dto.getStart() == null || dto.getEnd() == null) {
            return true;
        }
        return dto.getStart().isBefore(dto.getEnd());
    }
}
