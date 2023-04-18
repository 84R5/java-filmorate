package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static long id = 0;
    private final HashMap<Long, Film> films = new HashMap<>();

    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    public Film create(Film film) {
        if (!films.containsKey(film.getId())) {
            id++;
            film.setId(id);
        }
        films.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм не найден.");
        }
        films.put(film.getId(), film);
        return film;
    }

    public Film findFilmById(long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм не найден.");
        }
        return films.get(id);
    }
}

