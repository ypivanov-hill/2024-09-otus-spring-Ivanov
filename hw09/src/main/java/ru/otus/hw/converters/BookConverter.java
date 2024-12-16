package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.dto.BookCountByGenreDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;


    public BookDto bookToDto(Book book) {

        List<String> genresIdList = book.getGenres().stream()
                .map(genre -> genre.getId() /*new GenreDto(genre.getId(), genre.getName())*/)
                .toList();
        AuthorDto author = new AuthorDto(book.getAuthor().getId(), book.getAuthor().getFullName());
        List<BookCommentDto> commentList = List.of();
        return new BookDto(book.getId(), book.getTitle(),author.getId(), genresIdList);
    }

    public String bookCountByGenreToString(List<BookCountByGenreDto> records) {
        return records.stream()
                .map(record -> "Genre: %s, Count: %d".formatted(record.getId(), record.getCount()))
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", " + System.lineSeparator()));
    }

}
