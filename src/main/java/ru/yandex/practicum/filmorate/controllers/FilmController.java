package ru.yandex.practicum.filmorate.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


    @GetMapping
    public Collection<Film> getFilms() throws NotFoundException {
        log.debug("Входящий запрос на получение списка всех фильмов");
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) throws NotFoundException {
        log.debug("Входящий запрос на получение фильма по id = {}", id);
        return filmService.findFilmById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") int count,
                                       @RequestParam(required = false) Integer genreId,
                                       @RequestParam(required = false) Integer year) {
        log.debug("Входящий запрос на получение первых {} популярных фильмов с жанром {} за год {}", count, genreId, year);
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(value = "userId") Long userId,
                                     @RequestParam(value = "friendId") Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @PostMapping
    public Film createFilm(@Validated @RequestBody Film film) throws RuntimeException {
        log.debug("Входящий запрос на создание фильма");
        log.debug(film.toString());
        return filmService.create(film);
    }

    @DeleteMapping("{id}")
    public void deleteFilm(@PathVariable Long id) throws NotFoundException {
        log.debug("Входящий запрос на удаление фильма с id = {}", id);
        filmService.deleteFilmById(id);
    }

    @PutMapping
    public Film updateFilm(@Validated @RequestBody Film film) throws NotFoundException {
        log.debug("Входящий запрос на редактирование фильма");
        log.debug(film.toString());
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) throws NotFoundException {
        log.debug("Входящий запрос на проставление лайка пользователем с id = {} для фильма с id = {}", userId, id);
        filmService.addFilmLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) throws NotFoundException {
        log.debug("Входящий запрос на удаление лайка пользователем с id = {} для фильма с id = {}", userId, id);
        filmService.removeFilmLike(id, userId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable Long directorId,
                                       @RequestParam(defaultValue = "likes", required = false) String sortBy) {
        return filmService.getDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/search")
    public Collection<Film> getSearchFilms(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "") String by,
            @RequestParam(defaultValue = "10") int count) {
        log.debug("Входящий запрос на поиск фильма парметры запрос: '{}' запрос поиска: '{}' количесвто фильмов: '{}'",
                query, by, count);
        return filmService.getSearchFilms(query, by, count);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerError(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }
}
