package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final AuthorService authorService;

    private final CommentService commentService;

    private final BookService bookService;

    @GetMapping("/comment")
    public String findAllComments(@RequestParam String bookId, Model model) {

        addReferences(bookId, model);
        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authorOptions", authors);

        return "commentList";
    }

    @GetMapping("/comment/new")
    public String addNewComments(@RequestParam String bookId, Model model) {

        BookDto book = bookService.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book Entity Not Found"));
        model.addAttribute("book", book);

        return "commentNew";
    }

    @PostMapping("/comment/new")
    public String createComment(@RequestParam(required = true) String bookId,
                                @RequestParam(value = "new-text", defaultValue = "", required = false) String newText) {

        if (newText != null && !newText.isEmpty()) {
            commentService.insert(newText, bookId);
        }
        return "redirect:/comment?bookId=" + bookId;
    }


    @GetMapping("/comment/edit")
    public String editComment(@RequestParam(required = true) String bookId, Model model) {
        addReferences(bookId, model);
        return "commentEdit";
    }

    @PostMapping("/comment/edit")
    public String editComment(@RequestParam(value = "id", defaultValue = "", required = false) List<String> ids,
                              @RequestParam(value = "text", defaultValue = "", required = false) List<String> texts,
                              @RequestParam(required = true) String bookId) {

        for (String id : ids) {
            commentService.updateOrDelete(id, texts.get(ids.indexOf(id)), bookId);
        }
        return "redirect:/comment?bookId=" + bookId;
    }

    @DeleteMapping("/deleteById")
    public String deleteById(@RequestParam String id, @RequestParam String bookId, Model model) {
        commentService.deleteById(id);

        addReferences(bookId, model);

        return "redirect:/comment?bookId=" + bookId;
    }

    private void addReferences(String bookId, Model model) {
        BookDto book = bookService.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book Entity Not Found"));
        model.addAttribute("book", book);

        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authorOptions", authors);

        List<CommentDto> commentDtos = commentService.findByBookId(bookId);
        model.addAttribute("comments", commentDtos);
    }
}
