package ru.otus.hw.controller;

import jakarta.validation.Valid;
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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/")
    public String findAllBooks(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);

        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authorOptions", authors);

        List<GenreDto> genres = genreService.findAll();
        model.addAttribute("genresOptions", genres);
        return "bookList";
    }

    @DeleteMapping("/deleteBookById/{id}")
    public String deleteBookById(@PathVariable String id, Model model) {

        bookService.deleteById(id);
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);

        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authorOptions", authors);

        List<GenreDto> genres = genreService.findAll();
        model.addAttribute("genresOptions", genres);
        return "redirect:/";
    }

    @PostMapping("/edit")
    public String updateBook(@RequestParam(required = false) String id,
                             @ModelAttribute("book") BookDto bookDto) {
        if (id == null || "".equals(id)) {
            bookService.insert(bookDto.getTitle(),
                    bookDto.getAuthorId(),
                    bookDto.getGenreIds().stream().collect(Collectors.toSet()));
        } else {
            bookService.update(id,
                    bookDto.getTitle(),
                    bookDto.getAuthorId(),
                    bookDto.getGenreIds().stream().collect(Collectors.toSet()));
        }
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
        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authorOptions", authors);

        List<GenreDto> genres = genreService.findAll();
        model.addAttribute("genresOptions", genres);
        BookDto book = new BookDto(null, null, authors.get(0).getId(), List.of(genres.get(0).getId()));
        model.addAttribute("book", book);

        return "bookEdit";
    }

}
