package ru.otus.hw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.formatter.AuthorPropertyEditor;
import ru.otus.hw.formatter.GenrePropertyEditor;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @InitBinder(value="book")
    protected void initBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(GenreDto.class, new GenrePropertyEditor(genreService));
        binder.registerCustomEditor(AuthorDto.class, new AuthorPropertyEditor(authorService));
    }

    @GetMapping("/")
    public String findAllBooks(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "bookList";
    }
    @GetMapping("/deleteBookById/{id}")
    public String deleteBookById(@PathVariable String id, Model model) {

        bookService.deleteById(id);
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "bookList";
    }
    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable String id,
                             @Valid @ModelAttribute("book") BookDto bookDto,
                             BindingResult result
    ) {
        if (result.hasErrors()) {
            return "bookEdit";
        }
        bookService.update(bookDto);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editBookById(@PathVariable String id, Model model) {
        BookDto book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invalid book Id:" + id));
        model.addAttribute("book", book);
        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authorOptions", authors);

        List<GenreDto> genres = genreService.findAll();
        model.addAttribute("genresOptions", genres);

        return "bookEdit";
    }

    @GetMapping("/create")
    public String createBookById(Model model) {
        BookDto book = new BookDto(null,null,null,null,null);
        model.addAttribute("book", book);
        return "bookEdit";
    }

}
