package ru.yandex.practicum.filmorate.controllers;


import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.model.Constants.FILM_RATE_HI;
import static ru.yandex.practicum.filmorate.model.Constants.FILM_RATE_LO;

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
    public Collection<Film> getTop(
            @RequestParam(defaultValue = "10", required = false) Long count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year) throws NotFoundException {
        log.debug("Входящий запрос на получение первых {} популярных фильмов", count);
        return filmService.getTop(count, genreId, year);
    }

    @PostMapping
    public Film createFilm(@Validated @RequestBody Film film) throws RuntimeException {
        log.debug("Входящий запрос на создание фильма c id = {}", film.getId());
        return filmService.create(film);
    }

    @PutMapping
    public Film updateFilm(@Validated @RequestBody Film film) throws NotFoundException {
        log.debug("Входящий запрос на редактирование фильма c id = {}", film.getId());
        return filmService.update(film);
    }

    /*@DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Long id) throws NotFoundException {
        log.debug("Входящий запрос на удаление фильма с id = {}", id);
        filmService.delete(id);
    }*/

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId, @RequestParam(required = false, defaultValue = "10") @Range(min = FILM_RATE_LO, max = FILM_RATE_HI, message = "Rate range not valid.") Integer rate) throws NotFoundException {
        log.debug("Входящий запрос на проставление лайка пользователем с id = {} для фильма с id = {}", userId, id);
        filmService.addLike(id, userId, rate);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) throws NotFoundException {
        log.debug("Входящий запрос на удаление лайка пользователем с id = {} для фильма с id = {}", userId, id);
        filmService.removeFilmLike(id, userId);
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
