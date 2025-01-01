package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCountByGenreDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<BookDto> findById(String id);

    List<BookDto> findAll();

    BookDto insert(String title, AuthorDto author, Set<GenreDto> genres);

    BookDto update(String id, String title, AuthorDto author, Set<GenreDto> genres);

    void deleteById(String id);

    List<BookCountByGenreDto> getBookCountByGenre();
}
