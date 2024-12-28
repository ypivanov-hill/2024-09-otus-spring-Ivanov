package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class CommentDto {

    private String id;

    private String text;

    private BookDto book;

}