package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaStorage mpaStorage;

    public MpaController(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @GetMapping
    public List<MPA> getAllMPA() {
        return mpaStorage.getMPAList();
    }

    @GetMapping("/{id}")
    public MPA getMPAById(@PathVariable("id") int id) {
        if (mpaStorage.findMPAById(id) != null) {
            return mpaStorage.findMPAById(id);
        } else {
            throw new NotFoundException("Возрастной рейтинг не найден.");
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMPANotFound(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
