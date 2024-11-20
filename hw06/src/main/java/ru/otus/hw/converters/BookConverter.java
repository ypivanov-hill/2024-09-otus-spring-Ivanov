package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    public String bookToString(BookDto book) {
        var genresString = book.getGenres().stream()
                .map(genre -> "Id: %d, Name: %s".formatted(genre.getId(), genre.getName()))
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        var commentsString = book.getComments().stream()
                .map(comment -> "Id: %d, text: %s".formatted(comment.getId(), comment.getText()))
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        return "Id: %d, title: %s, author: {%s}, genres: [%s], comments: [%s]\"".formatted(
                book.getId(),
                book.getTitle(),
                authorConverter.authorToString(book.getAuthor()),
                genresString,
                commentsString);
    }

    public BookDto bookToDto(Book book) {

        List<GenreDto> genresList = book.getGenres().stream()
                .map(genre -> new GenreDto(genre.getId(), genre.getName()))
                .toList();
        AuthorDto author = new AuthorDto(book.getAuthor().getId(), book.getAuthor().getFullName());
        List<BookCommentDto> commentList = book.getComments().stream()
                .map(comment -> new BookCommentDto(comment.getId(), comment.getText()))
                .toList();
        return new BookDto(book.getId(), book.getTitle(),author, genresList, commentList);
    }

}
