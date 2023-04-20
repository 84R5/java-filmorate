package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;

@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("Received request to POST /directors with body: {}", director);
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        directorService.updateDirector(director);
        log.info("Received request to PUT /directors with body: {}", director);
        return director;
    }

    @DeleteMapping("/{directorId}")
    public void deleteDirector(@PathVariable Long directorId) {
        log.info("Received request to DELETE /directors/{}", directorId);
        directorService.deleteDirector(directorId);
    }

    @GetMapping("/{directorId}")
    public Director getDirectorById(@PathVariable Long directorId) {
        log.info("Received request to GET /directors/{}", directorId);
        return directorService.getDirector(directorId);
    }

    @GetMapping
    public Iterable<Director> getAllDirectors() {
        log.info("Received request to GET /directors");
        return directorService.getAllDirectors();
    }
}
