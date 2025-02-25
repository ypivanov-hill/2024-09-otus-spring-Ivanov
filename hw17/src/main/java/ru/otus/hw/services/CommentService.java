package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<CommentDto> findById(String id);

    List<CommentDto> findByBookId(String bookTitle);

    CommentDto insert(String text, BookDto book);

    CommentDto update(String id, String text, BookDto book);

    void deleteById(String id);
}
