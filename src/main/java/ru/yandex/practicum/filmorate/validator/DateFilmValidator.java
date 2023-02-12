package ru.yandex.practicum.filmorate.validator;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateFilmValidator implements ConstraintValidator<DateFilm, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        } else {
            return !value.isBefore(LocalDate.of(1895, 12, 28));
        }
    }
}