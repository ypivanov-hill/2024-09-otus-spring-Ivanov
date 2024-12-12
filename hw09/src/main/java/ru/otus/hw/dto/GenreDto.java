package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GenreDto {

    private final String id;

    private final String name;
}