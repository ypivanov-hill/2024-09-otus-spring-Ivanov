package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converters.CommentConvertor;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
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
    public List<CommentDto> findByBookTitle(String bookTitle) {
        Book book = bookRepository.findByTitleIgnoreCase(bookTitle)
                .orElseThrow(() -> new EntityNotFoundException("Book with title %s not found".formatted(bookTitle)));
        List<Comment> comments = commentRepository.findByBookId(book.getId());
        return comments.stream().map(commentConvertor::commentToCommentDto).toList();
    }

    @Override
    public CommentDto insert(String text, String bookTitle) {
        return save(null, text, bookTitle);
    }

    @Override
    public CommentDto update(String id, String text, String bookTitle) {
        return save(id, text, bookTitle);
    }

    @Override
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }

    private CommentDto save(String id, String text, String bookTitle) {
        var book = bookRepository.findByTitleIgnoreCase(bookTitle)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookTitle)));
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
