package ru.otus.hw.services;

import ru.otus.hw.dto.BookCountByGenreDto;
import ru.otus.hw.dto.BookDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<BookDto> findById(String id);

    List<BookDto> findAll();

    BookDto insert(String title, String authorFullName, Set<String> genreNames);

    BookDto update(String id, String title, String authorFullName, Set<String> genreNames);

    BookDto update(BookDto bookDto);

    void deleteById(String id);

    void deleteByTitle(String title);

    Optional<BookDto> findByTitleIgnoreCase(String title);

    List<BookCountByGenreDto> getBookCountByGenre();
}
