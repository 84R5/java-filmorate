package ru.yandex.practicum.filmorate.service.film;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.Constants.*;


@Data
@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film findFilmById(long id) {
        return filmStorage.findFilmById(id);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void addFilmLike(long filmId, long userId) {
        filmStorage.addFilmsLike(filmId, userId);
        Film film = filmStorage.findFilmById(filmId);
        film.setRate(film.getRate() + 1);
        userStorage.findUserById(userId).getFilmsLike().add(filmId);
    }

    public void removeFilmLike(long filmId, long userId) {
        filmStorage.removeFilmLike(filmId, userId);
        Film film = filmStorage.findFilmById(filmId);
        film.setRate(film.getRate() - 1);
        userStorage.findUserById(userId).getFilmsLike().remove(filmId);
    }

    public Collection<Film> getRecommendation(Long userId) throws NotFoundException {
        Map<Long, Integer> userFilmsRate = filmStorage.getUserFilmsRateFromLikes(userId);
        List<Long> crossFilmUserFromLike = filmStorage.getCrossFilmsUserFromLike(userId);

        List<Film> recommendedFilms = new ArrayList<>();

        for (Long crossUserId : crossFilmUserFromLike) {
            Map<Long, Integer> crossFilmRate = filmStorage.getUserFilmsRateFromLikes(crossUserId);
            if (countUserCrossFilm(crossFilmRate, userFilmsRate) == 0) {
                continue;
            }

            for (Map.Entry<Long, Integer> filmRate : crossFilmRate.entrySet()) {
                long filmId = filmRate.getKey();
                int rate = filmRate.getValue();

                if (rate >= FILM_RATE_AV && !userFilmsRate.containsKey(filmId)) {
                    recommendedFilms.add(findFilmById(filmId));
                }
            }
        }

        return recommendedFilms;
    }

    private int countUserCrossFilm(Map<Long, Integer> crossFilmRate, Map<Long, Integer> userFilmsRate) {
        int countCrossFilm = 0;

        for (Map.Entry<Long, Integer> filmRateEntry : userFilmsRate.entrySet()) {
            long filmId = filmRateEntry.getKey();
            if (crossFilmRate.containsKey(filmId)) {
                int originRateLo = Math.max(filmRateEntry.getValue() - FILM_RATE_DELTA, FILM_RATE_LO);
                originRateLo = Math.min(originRateLo, FILM_RATE_AV - 1);

                if (originRateLo >=  crossFilmRate.get(filmId)) {
                    countCrossFilm++;
                }
            }
        }
        return countCrossFilm;
    }
}
