package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.services.BookService;

import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class BookCommands {

    private final BookService bookService;

    private final BookConverter bookConverter;

    @ShellMethod(value = "Find all books", key = "ab")
    public String findAllBooks() {
        return bookService.findAll().stream()
                .map(bookConverter::bookToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find book by id", key = "bbid")
    public String findBookById(String id) {
        return bookService.findById(id)
                .map(bookConverter::bookToString)
                .orElse("Book with id %s not found".formatted(id));
    }

    // bbt BookTitle_3994
    @ShellMethod(value = "Find book by title", key = "bbt")
    public String findByTitleIgnoreCase(String title) {
        return bookService.findByTitleIgnoreCase(title)
                .map(bookConverter::bookToString)
                .orElse("Book with title %s not found".formatted(title));
    }

    // bins newBook Author_1 Genre_3,Genre_6
    @ShellMethod(value = "Insert book", key = "bins")
    public String insertBook(String title, String authorFullName, Set<String> genreNames) {
        var savedBook = bookService.insert(title, authorFullName, genreNames);
        return bookConverter.bookToString(savedBook);
    }

    // bupd 674ea27f8f83a1525ba5e98b editedBook Author_3 Genre_2,Genre_3
    @ShellMethod(value = "Update book", key = "bupd")
    public String updateBook(String id, String title, String authorFullName, Set<String> genreNames) {
        var savedBook = bookService.update(id, title, authorFullName, genreNames);
        return bookConverter.bookToString(savedBook);
    }

    // bdel 674ea27f8f83a1525ba5e98b
    @ShellMethod(value = "Delete book by id", key = "bdel")
    public void deleteBookById(String id) {
        bookService.deleteById(id);
    }

    // bdelt BookTitle_1
    @ShellMethod(value = "Delete book by title", key = "bdelt")
    public void deleteBookByTitle(String title) {
        bookService.deleteByTitle(title);
    }

    // bs
    @ShellMethod(value = "get books count over genres", key = "bs")
    public String getBookCountByGenre() {
        return bookConverter.bookCountByGenreToString(bookService.getBookCountByGenre());
    }


}
