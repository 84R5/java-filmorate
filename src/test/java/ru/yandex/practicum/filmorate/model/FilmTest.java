package ru.yandex.practicum.filmorate.model;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.validation.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.yandex.practicum.filmorate.model.Constants.MAX_LENGTH_DESCRIPTION_FILM;

public class FilmTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    private Film film;
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    void beforeEach() {
        film = new Film.FilmBuilder()
                .id(null)
                .name("name")
                .description("")
                .releaseDate(LocalDate.of(1999, 9, 9))
                .duration(99L)
                .build();
    }

    @DisplayName("MaxSize description validation")
    @Test
    void checkDescriptionValidation() {
        film.setDescription(Stream.generate(() -> ("*")).limit(MAX_LENGTH_DESCRIPTION_FILM + 1).collect(Collectors.joining()));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals("Максимальная длина описания — " + MAX_LENGTH_DESCRIPTION_FILM + " символов", violations.iterator().next().getMessage());
    }

    @DisplayName("NotBlank name validation")
    @Test
    void checkNameIsNotBlank() {
        film.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals("Название не может быть пустым;", violations.iterator().next().getMessage());
    }

    @DisplayName("Pozitive duration validation")
    @Test
    void checkPositiveDurationValidation() {
        film.setDuration(-1L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals("Продолжительность фильма должна быть положительной.", violations.iterator().next().getMessage());
    }
}
