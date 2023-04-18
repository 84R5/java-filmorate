package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorDbStorage directorDbStorage;

    public Director createDirector(Director director) {
        if (director.getName() == null) {
            throw new IllegalArgumentException("Имя должно быть задано.");
        }
        if (director.getName().isEmpty() || director.getName().isBlank()) {
            throw new ValidationException("Имя не должно быть пустым.");
        }
        return directorDbStorage.createDirector(director);
    }

    public void updateDirector(Director director) {
        if (directorDbStorage.getDirector(director.getId()) == null) {
            throw new ValidationException("Такой режиссер не существует.");
        }
        if (director.getName() == null) {
            throw new IllegalArgumentException("Имя должно быть задано.");
        }
        if (director.getName().isEmpty() || director.getName().isBlank()) {
            throw new ValidationException("Имя не должно быть пустым.");
        }
        directorDbStorage.updateDirector(director);
    }

    public void deleteDirector(Long directorId) {
        if (directorDbStorage.getDirector(directorId) == null) {
            throw new ValidationException("Такой режиссер не существует.");
        }
        directorDbStorage.deleteDirector(directorId);
    }

    public Director getDirector(Long directorId) {
        if (directorDbStorage.getDirector(directorId) == null) {
            throw new ValidationException("Такой режиссер не существует.");
        }
        return directorDbStorage.getDirector(directorId);
    }

    public Iterable<Director> getAllDirectors() {
        return directorDbStorage.getAllDirectors();
    }
}
