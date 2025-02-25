package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class BookCommentDto {

    private final Long id;

    private final String text;
}