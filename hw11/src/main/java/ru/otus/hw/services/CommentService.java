package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Mono<CommentDto> findById(String id);

    Flux<CommentDto> findByBookId(String bookTitle);

    Mono<CommentDto> insert(String text, BookDto book);

    Mono<CommentDto> update(String id, String text, BookDto book);

    void deleteById(String id);
}
