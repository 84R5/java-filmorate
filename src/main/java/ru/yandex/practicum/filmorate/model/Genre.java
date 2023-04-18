package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class Genre {
    @NotNull
    private int id;
    private String name;

    public Genre(int id, String genre) {
        this.id = id;
        this.name = genre;
    }
}
