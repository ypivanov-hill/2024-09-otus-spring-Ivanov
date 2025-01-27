package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthorDto {

    private final String id;

    private final String fullName;
}