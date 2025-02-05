package ru.otus.hw.services;

import ru.otus.hw.dto.BookCompliteDto;
import ru.otus.hw.dto.BookDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {

    Optional<BookDto>  findById(long id);

    List<BookCompliteDto>  findAll();

    void save(Long id, BookDto bookDto);

    BookDto insert(String title, long authorId, Set<Long> genresIds);

    BookDto update(long id, String title, long authorId, Set<Long> genresIds);

    void deleteById(long id);
}
