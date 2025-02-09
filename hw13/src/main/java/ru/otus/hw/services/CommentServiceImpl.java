package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConvertor;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentServiceSecured commentServiceSecured;

    private final CommentConvertor  commentConvertor;

    private final BookRepository bookRepository;

    private final AclServiceWrapperService aclServiceWrapperService;

    @Transactional(readOnly = true)
    @Override
    public Optional<CommentDto> findById(long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.map(commentConvertor::commentToCommentDto);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<CommentDto> findByBookId(long bookId) {
        List<Comment> comments = commentServiceSecured.findByBookIdSecured(bookId);
        return comments
                .stream()
                .map(commentConvertor::commentToCommentDto)
                .collect(Collectors.toCollection(ArrayList<CommentDto>::new));
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Override
    public CommentDto insert(String text, long bookId) {
        return save(0, text, bookId);
    }

    @Transactional
    @PreAuthorize("canWrite(#id, T(ru.otus.hw.models.Comment))")
    @Override
    public CommentDto update(long id, String text, long bookId) {
        return save(id, text, bookId);
    }

    @Transactional
    @PreAuthorize("canDelete(#id, T(ru.otus.hw.models.Comment))")
    @Override
    public void deleteById(long id) {
        aclServiceWrapperService.deletePermission("ru.otus.hw.models.Comment", id);
        commentRepository.deleteById(id);
    }

    @Transactional
    @PreAuthorize("canWrite(#id, T(ru.otus.hw.models.Comment))")
    @Override
    public void updateOrDelete(long id, String text, long bookId) {
        if (text == null || text.isEmpty()) {
            deleteById(id);
        } else {
            update(id, text, bookId);
        }
    }

    private CommentDto save(long id, String title, long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        var comment = new Comment(id, title, book);
        comment = commentRepository.save(comment);
        if (id == 0) {
            aclServiceWrapperService.createAllPermission(comment);
        }
        return commentConvertor.commentToCommentDto(comment);
    }
}
