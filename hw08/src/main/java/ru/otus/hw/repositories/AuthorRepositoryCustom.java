package ru.otus.hw.repositories;

import ru.otus.hw.models.Author;

import java.util.List;

public interface AuthorRepositoryCustom {

    void deleteByFullName(String fullName);

    List<Author> findByFullName(String fullName);

    Author findOneByFullName(String fullName);
}
