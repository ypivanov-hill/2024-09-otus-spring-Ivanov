package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class BookCommentDto {

    private final String id;

    private final String text;
}