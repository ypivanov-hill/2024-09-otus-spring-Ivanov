package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final BookService bookService;
    private final CommentService commentService;

    @GetMapping("/comment")
    public String findAllComments(@RequestParam String bookId, Model model) {
        List<CommentDto> commentDtos = commentService.findByBookId(bookId);
        model.addAttribute("comments", commentDtos);
        return "commentList";
    }

    ///deleteById/{id}
    @GetMapping("/deleteById")
    public String findAllComments(@RequestParam String id,@RequestParam String bookId, Model model) {
        commentService.deleteById(id);
        List<CommentDto> commentDtos = commentService.findByBookId(bookId);
        model.addAttribute("comments", commentDtos);
        return "commentList";
    }
}
