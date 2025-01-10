package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.CommentConvertor;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentConvertor  commentConvertor;

    private final BookRepository bookRepository;

    @Override
    public Mono<CommentDto> findById(String id) {
        Mono<Comment> comment = commentRepository.findById(id);
        return comment.map(commentConvertor::commentToCommentDto);
    }

    @Override
    public Flux<CommentDto> findByBookId(String id) {
        Flux<Comment> comments = commentRepository.findByBookId(id);
        return Flux.empty();//comments.flatMap(commentConvertor::commentToCommentDto);
    }

    @Override
    public Mono<CommentDto> insert(String text, BookDto book) {
        return save(null, text, book);
    }

    @Override
    public Mono<CommentDto> update(String id, String text,BookDto book) {
        return save(id, text, book);
    }

    @Override
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }

    private Mono<CommentDto> save(String id, String text, BookDto bookDto) {
        var book = bookRepository.findById(bookDto.getId());/*
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookDto.getId())));*/
        Comment comment;
        /*if (id == null) {
            comment = new Comment(text, book);
        } else {
            comment = commentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Comment with id %s not found".formatted(id)));
            comment.setText(text);
            comment.setBook(book);
        }

        return commentConvertor.commentToCommentDto(commentRepository.save(comment));*/
        return null;
    }
}
