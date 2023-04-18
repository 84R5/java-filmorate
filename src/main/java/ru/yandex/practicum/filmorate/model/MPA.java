package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder

public class MPA {
    @NotNull
    private int id;
    private String name;

    public MPA(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
