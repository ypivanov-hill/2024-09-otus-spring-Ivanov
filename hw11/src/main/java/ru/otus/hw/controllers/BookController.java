package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/api/v1/book")
    public ResponseEntity<Flux<BookDto>> findAllBooks() {
        Flux<BookDto> books = bookService.findAll();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/api/v1/book/{id}")
    public ResponseEntity<Mono<BookDto>> findBookById(@PathVariable String id) {
        Mono<BookDto> book = bookService.findById(id);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/api/v1/book/{id}")
    public ResponseEntity<String> deleteBookById(@PathVariable String id) {
        bookService.deleteById(id);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/api/v1/book")
    public ResponseEntity<Mono<BookDto>>  createBook(@RequestBody() BookDto bookDto) {
        Mono<BookDto> book =  bookService.insert(bookDto.getTitle(),
                    bookDto.getAuthor(),
                  new HashSet<>(bookDto.getGenres()));

        return ResponseEntity.ok(book);
    }

    @PutMapping("/api/v1/book")
    public ResponseEntity<Mono<BookDto>>  updateBook(@RequestBody() BookDto bookDto) {
        bookService.findById(bookDto.getId());
        Mono<BookDto> returnedBookDto =  bookService.update(bookDto.getId(),
                bookDto.getTitle(),
                bookDto.getAuthor(),
                new HashSet<>(bookDto.getGenres()));

        return ResponseEntity.ok(returnedBookDto);
    }
}
