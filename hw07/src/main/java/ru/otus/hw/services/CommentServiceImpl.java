package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConvertor;
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

    @Transactional(readOnly = true)
    @Override
    public Optional<CommentDto> findById(long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.map(commentConvertor::commentToCommentDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> findByBookId(long bookId) {
        List<Comment> comments = commentRepository.findByBookId(bookId);
        return comments.stream().map(commentConvertor::commentToCommentDto).toList();
    }

    @Transactional
    @Override
    public CommentDto insert(String text, long bookId) {
        return save(0, text, bookId);
    }

    @Transactional
    @Override
    public CommentDto update(long id, String text, long bookId) {
        return save(id, text, bookId);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    private CommentDto save(long id, String title, long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        var comment = new Comment(id, title, book);
        return commentConvertor.commentToCommentDto(commentRepository.save(comment));
    }
}
