package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class CommentDto {

    private final String id;

    private final String text;

    private final BookDto book;
}