package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCompliteDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
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

    @GetMapping("/")
    public String findAllBooks(Model model) {
        List<BookCompliteDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "bookList";
    }

    @DeleteMapping("/deleteBookById/{id}")
    public String deleteBookById(@PathVariable Long id, Model model) {

        bookService.deleteById(id);
        List<BookCompliteDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "redirect:/";
    }

    @PostMapping("/edit")
    public String updateBook(@RequestParam(required = false) Long id,
                             @ModelAttribute("book") BookDto bookDto) {
        bookService.save(id, bookDto);
        return "redirect:/";
    }


    @GetMapping("/edit/{id}")
    public String editBookById(@PathVariable Long id, Model model) {
        BookDto book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invalid book Id:" + id));
        model.addAttribute("book", book);
        addReferences(model);

        return "bookEdit";
    }

    @GetMapping("/create")
    public String createBookById(Model model) {
        addReferences(model);
        BookDto book = new BookDto(null, null, null, List.of());
        model.addAttribute("book", book);

        return "bookEdit";
    }

    private void addReferences(Model model) {
        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authorOptions", authors);

        List<GenreDto> genres = genreService.findAll();
        model.addAttribute("genresOptions", genres);
    }

}
