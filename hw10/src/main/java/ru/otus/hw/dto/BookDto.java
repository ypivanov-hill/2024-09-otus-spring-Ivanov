package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class BookDto {

    private final String id;

    private final String title;

    private final AuthorDto author;

    private final List<GenreDto> genres;

    private final List<BookCommentDto> comments;
}