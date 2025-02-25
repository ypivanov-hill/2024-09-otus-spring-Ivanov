package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthorDto {

    private final Long id;

    private final String fullName;
}