package ru.otus.hw.services;

import ru.otus.hw.dto.BookCompliteDto;
import ru.otus.hw.dto.BookCountByGenreDto;
import ru.otus.hw.dto.BookDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<BookDto> findById(String id);

    List<BookCompliteDto> findAll();

    BookDto insert(String title, String authorId, Set<String> genreIds);

    BookDto update(String id, String title, String authorId, Set<String> genreIds);


    void deleteById(String id);

    List<BookCountByGenreDto> getBookCountByGenre();

    BookDto save(String id, BookDto bookDto);
}
