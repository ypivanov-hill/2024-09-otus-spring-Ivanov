package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.aspect.RateLimitedAndCircuitBreaker;
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
    @CircuitBreaker(name = "defaultCircuitBreaker")
    @RateLimiter(name = "defaultRateLimiter")
    //@RateLimitedAndCircuitBreaker(rateLimiterName = "bookRateLimiter", circuitBreakerName = "bookCircuitBreaker")
    public Optional<CommentDto> findById(String id) {
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.map(commentConvertor::commentToCommentDto);
    }

    @Override
    public List<CommentDto> findByBookId(String id) {
        List<Comment> comments = commentRepository.findByBookId(id);
        return comments.stream().map(commentConvertor::commentToCommentDto).toList();
    }

    @Override
    public CommentDto insert(String text, BookDto book) {
        return save(null, text, book);
    }

    @Override
    public CommentDto update(String id, String text,BookDto book) {
        return save(id, text, book);
    }

    @Override
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }

    private CommentDto save(String id, String text, BookDto bookDto) {
        var book = bookRepository.findById(bookDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookDto.getId())));
        Comment comment;
        if (id == null) {
            comment = new Comment(text, book);
        } else {
            comment = commentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Comment with id %s not found".formatted(id)));
            comment.setText(text);
            comment.setBook(book);
        }

        return commentConvertor.commentToCommentDto(commentRepository.save(comment));
    }
}
