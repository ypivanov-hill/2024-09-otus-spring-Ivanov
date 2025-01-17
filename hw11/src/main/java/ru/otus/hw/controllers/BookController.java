package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;

import java.util.HashSet;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping(path = "/api/v1/book")
    public Flux<BookDto> findAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/api/v1/book/{id}")
    public Mono<BookDto> findBookById(@PathVariable String id) {
        return bookService.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Entity Not Found")));
    }

    @DeleteMapping("/api/v1/book/{id}")
    public ResponseEntity<String> deleteBookById(@PathVariable String id) {
        bookService.deleteById(id);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/api/v1/book")
    public Mono<BookDto>  createBook(@RequestBody() BookDto bookDto) {

        return bookService.insert(bookDto.getTitle(),
                    bookDto.getAuthor(),
                  new HashSet<>(bookDto.getGenres()));
    }

    @PutMapping("/api/v1/book")
    public Mono<BookDto>  updateBook(@RequestBody() BookDto bookDto) {
        return  bookService.update(bookDto.getId(),
                bookDto.getTitle(),
                bookDto.getAuthor(),
                new HashSet<>(bookDto.getGenres()));
    }
}
