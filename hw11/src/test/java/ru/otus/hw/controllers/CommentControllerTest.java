package ru.otus.hw.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import reactor.test.StepVerifier;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@WebFluxTest
@TestPropertySource(properties = "mongock.enabled=false")
public class CommentControllerTest {

    @Autowired
    private WebTestClient webTestClient;


    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private GenreService genreService;

    @Autowired
    private ObjectMapper mapper;

    private AuthorDto author = new AuthorDto("AuthorId1", "Author 1 FullName");
    private List<GenreDto> genres = List.of(new GenreDto("GenreId1", "Genre1"),
            new GenreDto("GenreId2", "Genre2"),
            new GenreDto("GenreId3", "Genre3"),
            new GenreDto("GenreId4", "Genre4"));

    private BookDto book = new BookDto("BoolId1", "TestBook1", author, List.of(genres.get(0), genres.get(1)));

    private List<CommentDto> comments = List.of(new CommentDto("CommentId1", "Comment 1", book),
            new CommentDto("CommentId2", "Comment 2", book));

    @DisplayName("должен отображать спиcок список комментариев")
    @Test
    void shouldReturnCommentsByBook() throws Exception {
        when(bookService.findById(book.getId())).thenReturn(Mono.just(book));
        when(authorService.findAll()).thenReturn(Flux.just(author));
        Flux<CommentDto> commentFlux = Flux
                .fromIterable(comments)
                .log();
        when(commentService.findByBookId(book.getId())).thenReturn(commentFlux);
        int expectedSize = commentFlux.collectList().block().size();

        var client = webTestClient.mutate().build();

        var result = client
                .get().uri("/api/v1/book/{id}/comment", book.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CommentDto.class)
                .getResponseBody();

        StepVerifier
                .create(result)
                .assertNext(c -> {
                    assertThat(c).isNotNull();
                    try {
                        String resultJson = mapper.writeValueAsString(c);
                        String expectedJson = mapper.writeValueAsString(comments.get(0));
                        assertThat(resultJson).isEqualTo(expectedJson);
                    } catch (JsonProcessingException e) {
                        fail(e.getMessage());
                    }
                })
                .expectNextCount(expectedSize - 1)
                .expectComplete()
                .verify();
    }


    @DisplayName("должен удалять комментарий")
    @Test
    void shouldDeleteCommentAndReturnCommentList() throws Exception {
        when(bookService.findById(book.getId())).thenReturn(Mono.just(book));
        Flux<CommentDto> commentFlux = Flux
                .fromIterable(comments)
                .log();
        when(commentService.findByBookId(book.getId())).thenReturn(commentFlux);

        var client = webTestClient.mutate().build();

        client
                .delete().uri("/api/v1/book/{id}/comment/{id}", book.getId(), comments.get(0).getId())
                .exchange()
                .expectStatus()
                .isOk();

        verify(commentService, times(1)).deleteById(comments.get(0).getId());
    }

    @DisplayName("должен изменять комментарий")
    @Test
    void shouldEditCommentAndReturnCommentsList() throws Exception {

        Flux<CommentDto> commentFlux = Flux
                .fromIterable(comments)
                .log();
        when(commentService.findByBookId(book.getId())).thenReturn(commentFlux);

        CommentDto newComment = comments.get(1);
        newComment.setText("Some Comment");


        when(commentService.update(any(String.class), any(String.class), any(BookDto.class))).thenReturn(Mono.just(newComment));


        var client = webTestClient.mutate().build();

        client
                .put().uri("/api/v1/book/{bookId}/comment", book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(newComment))
                .exchange()
                .expectBody()
                .json(mapper.writeValueAsString(newComment));

        verify(commentService, times(1)).update(any(String.class), any(String.class), any(BookDto.class));
    }

    @DisplayName("должен создавать новый комментарий")
    @Test
    void shouldInsertCommentAndReturnCommentsList() throws Exception {
        Flux<CommentDto> commentFlux = Flux
                .fromIterable(comments)
                .log();
        when(commentService.findByBookId(book.getId())).thenReturn(commentFlux);

        CommentDto newComment = new CommentDto(null, "Some Comment", book);

        when(commentService.insert(any(String.class), any(BookDto.class))).thenReturn(Mono.just(newComment));

        var client = webTestClient.mutate().build();

        client
                .post().uri("/api/v1/book/{bookId}/comment", book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(newComment))
                .exchange()
                .expectBody()
                .json(mapper.writeValueAsString(newComment));

        verify(commentService, times(1)).insert(any(String.class), any(BookDto.class));
    }
}