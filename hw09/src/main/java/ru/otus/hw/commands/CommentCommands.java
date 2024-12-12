package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.converters.CommentConvertor;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
//@ShellComponent
public class CommentCommands {
    private final CommentService commentService;

    private final CommentConvertor commentConvertor;

    //@ShellMethod(value = "Find comment by id", key = "cid")
    public String findCommentById(String id) {
        return commentService.findById(id)
                .map(commentConvertor::commentToString)
                .orElse("Comment with id %s not found".formatted(id));
    }

    //@ShellMethod(value = "Find comment by Book id", key = "cbid")
    public String findAllByBookId(String bookId) {
        return commentService.findByBookId(bookId).stream()
                .map(commentConvertor::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    //cbt BookTitle_3994
    //@ShellMethod(value = "Find comment by Book Title", key = "cbt")
    public String findByBookTitle(String bookTitle) {
        return commentService.findByBookTitle(bookTitle).stream()
                .map(commentConvertor::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    // cins textTextInserted BookTitle_1
    //@ShellMethod(value = "Insert book", key = "cins")
    public String insertBook(String text, String bookTitle) {
        var savedComment = commentService.insert(text, bookTitle);
        return commentConvertor.commentToString(savedComment);
    }

    // cupd 675005e9f1fcf50887b87b0b textTextUpdated BookTitle_3994
    //@ShellMethod(value = "Update book", key = "cupd")
    public String updateBook(String id, String text, String bookTitle) {
        var savedComment = commentService.update(id, text, bookTitle);
        return commentConvertor.commentToString(savedComment);
    }

    // cdel 675005e9f1fcf50887b87b0b
    //@ShellMethod(value = "Delete comment by id", key = "cdel")
    public void deleteComment(String id) {
        commentService.deleteById(id);
    }
}
