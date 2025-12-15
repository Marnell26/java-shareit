package ru.practicum.shareit.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

public class DateValidator implements ConstraintValidator<StartBeforeEnd, BookingCreateDto> {
    @Override
    public boolean isValid(BookingCreateDto dto, ConstraintValidatorContext context) {
        if (dto.getStart() == null || dto.getEnd() == null) {
            return true;
        }
        return dto.getStart().isBefore(dto.getEnd());
    }
}
