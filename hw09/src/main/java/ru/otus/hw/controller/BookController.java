package ru.otus.hw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;
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
                             @RequestBody String text,
                             @Valid @ModelAttribute("book") BookDto bookDto,
                             //@Valid @ModelAttribute("GenreDto") List<GenreDto> GenreDto,
                             BindingResult bindingResult/*,
                             @RequestParam(value = "GenreDto", defaultValue = "")  List<GenreDto> genreIds*/
    ) {
        log.info("Book id {}", id);
        log.info("Book bookDto {}", text);
        log.info("authorId {}", bookDto.getAuthor().getId());
        log.info("genreIds {}", bookDto.getGenres().toString());
        log.info("genreIds size {}", bookDto.getGenres().size());
        var savedBook = bookService.update(id, bookDto.getTitle(), bookDto.getAuthor().getId(), bookDto.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet()));
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
