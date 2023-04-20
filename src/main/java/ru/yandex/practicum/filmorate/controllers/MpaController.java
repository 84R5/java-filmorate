package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    private final FilmService filmService;

    @GetMapping
    public List<MPA> getAllMPA() {
        log.info("Received request to GET /mpa");
        return filmService.getAllMPA();
    }

    @GetMapping("/{id}")
    public MPA getMPAById(@PathVariable("id") int id) {
        log.info("Received request to GET GET /mpa/{}", id);
        return filmService.getMPAById(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMPANotFound(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
