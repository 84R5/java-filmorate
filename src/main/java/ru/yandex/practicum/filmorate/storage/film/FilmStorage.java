package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getFilms();

    Film create(Film film);

    Film updateFilm(Film film);

    Film findFilmById(long id);

    List<Film> getDirectorFilms(Long directorId, String sortBy);

    List<Film> getCommonFilms(Long userId, Long friendId);

    void deleteFilmById(long id);
}