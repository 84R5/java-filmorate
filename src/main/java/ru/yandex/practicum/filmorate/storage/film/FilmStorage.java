package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film add(Film film);

    void delete(Long id);

    Film getById(Long id);

    Collection<Film> getFilms();
}
