package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConvertor;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {
    private final CommentService commentService;

    private final CommentConvertor commentConvertor;

    @ShellMethod(value = "Find comment by id", key = "cid")
    public String findCommentById(long id) {
        return commentService.findById(id)
                .map(commentConvertor::commentToString)
                .orElse("Comment with id %d not found".formatted(id));
    }

    @ShellMethod(value = "Find comment by Book id", key = "cbid")
    public String findAllByBookId(long bookId) {
        return commentService.findByBookId(bookId).stream()
                .map(commentConvertor::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    // cins textTextInserted 4
    @ShellMethod(value = "Insert book", key = "cins")
    public String insertBook(String text, long bookId) {
        var savedComment = commentService.insert(text, bookId);
        return commentConvertor.commentToString(savedComment);
    }

    // cupd 5 textTextUpdated 3
    @ShellMethod(value = "Update book", key = "cupd")
    public String updateBook(long id, String text, long bookId) {
        var savedComment = commentService.update(id, text, bookId);
        return commentConvertor.commentToString(savedComment);
    }

    // cdel 4
    @ShellMethod(value = "Delete book by id", key = "cdel")
    public void deleteBook(long id) {
        commentService.deleteById(id);
    }
}
