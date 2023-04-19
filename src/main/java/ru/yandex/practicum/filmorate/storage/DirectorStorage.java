package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Long directorId);

    Director getDirector(Long id);

    Iterable<Director> getAllDirectors();

    List<Director> findDirectorsByFilm(long filmId);
}
