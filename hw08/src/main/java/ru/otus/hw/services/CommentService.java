package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<CommentDto> findById(String id);

    List<CommentDto> findByBookId(String bookTitle);

    List<CommentDto> findByBookTitle(String bookTitle);

    CommentDto insert(String text, String bookTitle);

    CommentDto update(String id, String text, String bookTitle);

    void deleteById(String id);
}
