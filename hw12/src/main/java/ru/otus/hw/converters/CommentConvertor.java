package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

import java.util.Collections;

@RequiredArgsConstructor
@Component
public class CommentConvertor {

    public CommentDto commentToCommentDto(Comment comment) {
        BookDto bookDto = new BookDto(comment.getBook().getId(),
                comment.getBook().getTitle(),
                comment.getBook().getAuthor().getId(),
                Collections.emptyList());
        return new CommentDto(comment.getId(), comment.getText(), bookDto);
    }
}
