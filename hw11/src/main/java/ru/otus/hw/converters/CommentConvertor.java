package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

import java.util.Collections;

@RequiredArgsConstructor
@Component
public class CommentConvertor {

    public String commentToString(CommentDto comment) {
        return "Id: %s, Text: %s, Book: %s, Author: %s"
                .formatted(comment.getId(),
                        comment.getText(),
                        comment.getBook().getTitle(),
                        comment.getBook().getAuthor().getFullName());
    }

    public CommentDto commentToCommentDto(Comment comment) {
        /*AuthorDto authorDto = new AuthorDto(comment.getBook().getAuthor().getId(),
                comment.getBook().getAuthor().getFullName());
        BookDto bookDto = new BookDto(comment.getBook().getId(),
                comment.getBook().getTitle(),
                authorDto,
                Collections.emptyList());
        return new CommentDto(comment.getId(), comment.getText(), bookDto);*/
        return null;
    }
}
