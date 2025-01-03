package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class BookDto {

    private final String id;

    private final String title;

    private final String authorId;

    private final List<String> genreIds;

}