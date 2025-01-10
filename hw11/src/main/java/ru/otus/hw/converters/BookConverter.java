package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCountByGenreDto;
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
                .map(genre -> "Id: %s, Name: %s".formatted(genre.getId(), genre.getName()))
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        return "Id: %s, title: %s, author: {%s}, genres: [%s]".formatted(
                book.getId(),
                book.getTitle(),
                authorConverter.authorToString(book.getAuthor()),
                genresString);
    }

    public BookDto bookToDto(Book book) {
//TODO
        /*List<GenreDto> genresList = book.getGenres()
                .flatMap(genre -> new GenreDto(genre.getId(), genre.getName()))
                .toList();
        AuthorDto author = new AuthorDto(book.getAuthor().getId(), book.getAuthor().getFullName());
        return new BookDto(book.getId(), book.getTitle(),author, genresList);*/
        return null;
    }

    public String bookCountByGenreToString(List<BookCountByGenreDto> records) {
        return records.stream()
                .map(record -> "Genre: %s, Count: %d".formatted(record.getId(), record.getCount()))
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", " + System.lineSeparator()));
    }

}
