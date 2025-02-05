package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class BookDto {

    private final Long id;

    private final String title;

    private final Long authorId;

    private final List<Long> genreIds;

}