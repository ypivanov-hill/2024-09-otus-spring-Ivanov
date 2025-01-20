package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.*;

import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest
@TestPropertySource(properties = "mongock.enabled=false")
public class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private CommentService commentService;

    private List<AuthorDto> authors = List.of(new AuthorDto("AuthorId1", "Author 1 FullName"),
            new AuthorDto("AuthorId2", "Author 2 FullName"));

    private List<GenreDto> genres = List.of(new GenreDto("GenreId1", "Genre1"),
            new GenreDto("GenreId2", "Genre2"),
            new GenreDto("GenreId3", "Genre3"),
            new GenreDto("GenreId4", "Genre4"));

    private List<BookDto> books = List.of(new BookDto("BoolId1", "TestBook1", authors.get(0), List.of(genres.get(0),genres.get(1)) ),
            new BookDto("2L", "TestBook2", authors.get(1), List.of(genres.get(2),genres.get(3)) ));



    @DisplayName("должен возвращать спиcок книг")
    @Test
    void shouldReturnStatusOkAndCorrectAllBooksContent() throws Exception {
        Flux<BookDto> booksFlux = Flux
                .fromIterable(books)
                .log();

        when(bookService.findAll()).thenReturn(booksFlux);

        var client = webTestClient.mutate().build();

        client
                .get().uri("/api/v1/book")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(mapper.writeValueAsString(books));
    }

    @DisplayName("должен возвращать книгу по ид ")
    @Test
    void shouldReturnStatusOkAndCorrectBookById() throws Exception {
        BookDto expectedBook = books.get(0);
        when(bookService.findById(expectedBook.getId())).thenReturn(Mono.just(expectedBook));

        var client = webTestClient.mutate().build();

        client
                .get().uri("/api/v1/book/{id}", expectedBook.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(mapper.writeValueAsString(expectedBook));
    }


    @DisplayName("должен отображать ошибку")
    @Test
    void shouldReturnErrorWhenBookNotFound() throws Exception {
        when(bookService.findById(books.get(0).getId() + "wrong")).thenReturn(Mono.error(new EntityNotFoundException("")));

        var client = webTestClient.mutate().build();

        client
                .get().uri("/api/v1/book/{id}", books.get(0).getId() + "wrong")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @DisplayName("должен изменять книгу")
    @Test
    void shouldUpdateBookAndReturnNewBook() throws Exception {
        BookDto expectedBook = books.get(0);
        when(bookService.findById(expectedBook.getId()))
                .thenReturn(Mono.just(expectedBook));

        BookDto newBook = new BookDto(expectedBook.getId(),
                "New Title",
                expectedBook.getAuthor(),
                expectedBook.getGenres());

        when(bookService.update(any(String.class),
                any(String.class),
                any(AuthorDto.class),
                any(HashSet.class)))
                .thenReturn(Mono.just(newBook));

        var client = webTestClient.mutate().build();

        client
                .put().uri("/api/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(newBook))
                .exchange()
                .expectBody()
                .json(mapper.writeValueAsString(newBook));

        verify(bookService, times(1))
                .update(any(String.class),
                        any(String.class),
                        any(AuthorDto.class),
                        any(HashSet.class));
    }

}