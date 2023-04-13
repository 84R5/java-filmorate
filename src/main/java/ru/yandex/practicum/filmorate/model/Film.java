package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.DateFilm;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

import static ru.yandex.practicum.filmorate.model.Constants.MAX_LENGTH_DESCRIPTION_FILM;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film  {
    private int rate;
    private Long id;
    @NotBlank(message = "Название не может быть пустым;")
    private String name;
    @Size(max = MAX_LENGTH_DESCRIPTION_FILM, message = "Максимальная длина описания — " + MAX_LENGTH_DESCRIPTION_FILM + " символов")
    private String description;
    private MPA mpa;
    private List<Genre> genres;
    @DateFilm(message = "дата релиза — не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private Long duration;


}

