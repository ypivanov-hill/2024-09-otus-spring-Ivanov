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
    public ResponseEntity<List<BookDto>> findAllBooks() {
        List<BookDto> books = bookService.findAll();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/api/v1/book/{id}")
    public ResponseEntity<BookDto> findBookById(@PathVariable String id) {
        BookDto book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/api/v1/book/{id}")
    public ResponseEntity<List<BookDto>> deleteBookById(@PathVariable String id) {

        bookService.deleteById(id);
        List<BookDto> books = bookService.findAll();
        return ResponseEntity.ok(books);
    }

    @PostMapping("/api/v1/book")
    public ResponseEntity<BookDto>  createBook(@RequestBody() BookDto bookDto) {
          BookDto book =  bookService.insert(bookDto.getTitle(),
                    bookDto.getAuthor(),
                  new HashSet<>(bookDto.getGenres()));

        return ResponseEntity.ok(book);
    }

    @PutMapping("/api/v1/book")
    public ResponseEntity<BookDto>  updateBook(@RequestBody() BookDto bookDto) {
        bookService.findById(bookDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Invalid book Id:" + bookDto.getId()));
        BookDto returnedBookDto =  bookService.update(bookDto.getId(),
                bookDto.getTitle(),
                bookDto.getAuthor(),
                new HashSet<>(bookDto.getGenres()));

        return ResponseEntity.ok(returnedBookDto);
    }
}
