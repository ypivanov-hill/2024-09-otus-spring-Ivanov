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

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentConvertor commentConvertor;

    private final BookRepository bookRepository;

    @Override
    public Mono<CommentDto> findById(String id) {
        return commentRepository.findById(id).map(commentConvertor::commentToCommentDto);
    }

    @Override
    public Flux<CommentDto> findByBookId(String id) {
        return commentRepository.findByBookId(id).map(commentConvertor::commentToCommentDto);
    }

    @Override
    public Mono<CommentDto> insert(String text, BookDto book) {
        return save(null, text, book);
    }

    @Override
    public Mono<CommentDto> update(String id, String text, BookDto book) {
        return save(id, text, book);
    }

    @Override
    public void deleteById(String id) {
        commentRepository.deleteById(id).subscribe();
    }

    private Mono<CommentDto> save(String id, String text, BookDto bookDto) {
        Mono<Comment> commentMono = bookRepository.findById(bookDto.getId())
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Book with id %s not found".formatted(bookDto.getId())))
                )
                .flatMap(book -> {
                    Mono<Comment> comment;
                    if (id == null) {
                        comment = commentRepository.save(new Comment(text, book));
                    } else {
                        comment = commentRepository.findById(id)
                                .flatMap(comment1 -> {
                                    comment1.setText(text);
                                    comment1.setBook(book);
                                   return commentRepository.save(comment1);
                                });
                    }
                    return comment;
                });
        return commentMono.map(commentConvertor::commentToCommentDto);
    }
}
