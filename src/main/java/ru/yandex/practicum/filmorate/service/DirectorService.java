package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorDbStorage directorDbStorage;

    public Director createDirector(Director director) {
        if (director.getName() == null) {
            log.warn("Name must be set");
            throw new IllegalArgumentException("Name must be set");
        }
        if (director.getName().isEmpty() || director.getName().isBlank()) {
            log.warn("Name cannot be empty");
            throw new ValidationException("Name cannot be empty");
        }
        return directorDbStorage.createDirector(director);
    }

    public void updateDirector(Director director) {
        if (directorDbStorage.getDirector(director.getId()) == null) {
            log.warn("Director not found");
            throw new ValidationException("Director not found");
        }
        if (director.getName() == null) {
            log.warn("Name must be set");
            throw new IllegalArgumentException("Name must be set");
        }
        if (director.getName().isEmpty() || director.getName().isBlank()) {
            log.warn("Name cannot be empty");
            throw new ValidationException("Name cannot be empty");
        }
        directorDbStorage.updateDirector(director);
    }

    public void deleteDirector(Long directorId) {
        if (directorDbStorage.getDirector(directorId) == null) {
            log.warn("Director not found");
            throw new ValidationException("Director not found");
        }
        directorDbStorage.deleteDirector(directorId);
    }

    public Director getDirector(Long directorId) {
        if (directorDbStorage.getDirector(directorId) == null) {
            log.warn("Director not found");
            throw new ValidationException("Director not found");
        }
        return directorDbStorage.getDirector(directorId);
    }

    public Iterable<Director> getAllDirectors() {
        return directorDbStorage.getAllDirectors();
    }
}
