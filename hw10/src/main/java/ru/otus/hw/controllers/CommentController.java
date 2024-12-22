package ru.otus.hw.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final BookService bookService;

    @GetMapping("/api/v1/book/{bookId}/comments")
    public ResponseEntity<List<CommentDto>> findAllComments(@PathVariable String bookId) {

        BookDto book = bookService.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book Entity Not Found"));


        List<CommentDto> commentDtos = commentService.findByBookId(bookId);

        return ResponseEntity.ok(commentDtos);
    }


    @PostMapping("/comment/edit")
    public String editComment(@RequestParam(value = "id", defaultValue = "", required = false) List<String> ids,
                              @RequestParam(value = "text", defaultValue = "", required = false) List<String> texts,
                              @RequestParam(required = true) String bookId,
                              @RequestParam(value = "new-text", defaultValue = "", required = false) String newText) {

        if (newText != null && !newText.isEmpty()) {
            commentService.insert(newText, bookId);
        }
        for (String id : ids) {
            String text = texts.get(ids.indexOf(id));
            if (text == null || text.isEmpty()) {
                commentService.deleteById(id);
            } else {
                commentService.update(id, text, bookId);
            }
        }
        return "redirect:/comment?bookId=" + bookId;
    }
    //TODO: add delete comment
    @GetMapping("/deleteById")
    public String findAllComments(@RequestParam String id, @RequestParam String bookId) {
        commentService.deleteById(id);



        return "redirect:/comment?bookId=" + bookId;
    }
}
