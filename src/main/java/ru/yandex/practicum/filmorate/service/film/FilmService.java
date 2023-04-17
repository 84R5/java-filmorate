package ru.yandex.practicum.filmorate.service.film;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Data
@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;
    private final DirectorStorage directorStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("feedDbStorage") FeedStorage feedStorage,
                       @Qualifier("directorDbStorage") DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
        this.directorStorage = directorStorage;
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

    public void deleteFilmById(long filmId) {
        filmStorage.deleteFilmById(filmId);
    }

    public List<Film> getPopular(int count, Integer genreId, Integer year) {
        return filmStorage.getFilms().stream()
                .filter(f -> filterPopular(f, genreId, year))
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean filterPopular(Film f, Integer genreId, Integer year) {
        if (genreId != null) {
            if (year != null) {
                return f.getGenres().stream()
                        .filter(Genre -> Genre.getId() == genreId).count() == 1 && f.getReleaseDate().getYear() == year;
            } else return f.getGenres().stream()
                    .filter(Genre -> Genre.getId() == genreId).count() == 1;
        } else if (year != null) {
            return f.getReleaseDate().getYear() == year;
        } else {
            return true;
        }
    }


    public void addFilmLike(long filmId, long userId) {
        userStorage.addFilmsLike(filmId, userId);
        Film film = filmStorage.findFilmById(filmId);
        film.setRate(film.getRate() + 1);
        userStorage.findUserById(userId).getFilmsLike().add(filmId);
        feedStorage.addFeed(Feed.builder()
                .operation(FeedOperation.ADD)
                .eventType(FeedEventType.LIKE)
                .entityId(filmId)
                .userId(userId)
                .build());
    }

    public void removeFilmLike(long filmId, long userId) {
        userStorage.removeFilmLike(filmId, userId);
        Film film = filmStorage.findFilmById(filmId);
        film.setRate(film.getRate() - 1);
        userStorage.findUserById(userId).getFilmsLike().remove(filmId);
        feedStorage.addFeed(Feed.builder()
                .operation(FeedOperation.REMOVE)
                .eventType(FeedEventType.LIKE)
                .entityId(filmId)
                .userId(userId)
                .build());
    }

    public List<Film> getDirectorFilms(Long directorId, String sortBy) {

        if (!(sortBy.equals("year") || sortBy.equals("likes"))) {
            throw new IllegalArgumentException("Сортировка возможна либо по годам, либо по количеству лайков");
        }

        if (directorStorage.getDirector(directorId) == null) {
            throw new IllegalArgumentException("Режиссер не найден.");
        }

        return filmStorage.getDirectorFilms(directorId, sortBy);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {

        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getSearchFilms(String query, String by, int count) {
        return filmStorage.getSearchFilms(query, by, count);
    }
}
