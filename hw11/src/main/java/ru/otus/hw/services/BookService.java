package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCountByGenreDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;

import java.util.Set;

public interface BookService {
    Mono<BookDto> findById(String id);

    Flux<BookDto> findAll();

    Mono<BookDto> insert(String title, AuthorDto author, Set<GenreDto> genres);

    Mono<BookDto> update(String id, String title, AuthorDto author, Set<GenreDto> genres);

    Mono<String> deleteById(String id);

    Flux<BookCountByGenreDto> getBookCountByGenre();
}
