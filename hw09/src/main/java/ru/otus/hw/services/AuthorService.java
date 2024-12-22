package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    List<AuthorDto> findAll();

    Optional<AuthorDto> findById(String id);

    List<Author> findByFullName(String fullName);

    void deleteByFullName(String fullName);
}
