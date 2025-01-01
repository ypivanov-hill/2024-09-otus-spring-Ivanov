package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = CommentController.class)
@TestPropertySource(properties = "mongock.enabled=false")
public class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper mapper;

    private AuthorDto author = new AuthorDto("AuthorId1", "Author 1 FullName");
    private List<GenreDto> genres = List.of(new GenreDto("GenreId1", "Genre1"),
            new GenreDto("GenreId2", "Genre2"),
            new GenreDto("GenreId3", "Genre3"),
            new GenreDto("GenreId4", "Genre4"));

    private BookDto book = new BookDto("BoolId1", "TestBook1", author, List.of(genres.get(0),genres.get(1)));

    private List<CommentDto> comments = List.of(new CommentDto("CommentId1", "Comment 1", book),
            new CommentDto("CommentId2", "Comment 2", book));

    @DisplayName("должен отображать спиcок список комментариев")
    @Test
    void shouldReturnCommentsByBook() throws Exception {
        when(bookService.findById(book.getId())).thenReturn(Optional.ofNullable(book));
        when(authorService.findAll()).thenReturn(List.of(author));
        when(commentService.findByBookId(book.getId())).thenReturn(comments);

        mvc.perform(get("/api/v1/book/{id}/comment", book.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(comments)));
    }

    @DisplayName("должен отображать ошибку")
    @Test
    void shouldReturnErrorWhenBookNotFound() throws Exception {
        when(bookService.findById(book.getId() + "wrong")).thenThrow(new EntityNotFoundException(""));
        mvc.perform(get("/api/v1/book/{id}/comment", book.getId() + "wrong"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Entity not found("));
    }

    @DisplayName("должен удалять комментарий")
    @Test
    void shouldDeleteCommentAndReturnCommentList() throws Exception {
        when(bookService.findById(book.getId())).thenReturn(Optional.ofNullable(book));
        when(commentService.findByBookId(book.getId())).thenReturn(comments);

        mvc.perform(delete("/api/v1/book/{id}/comment/{id}", book.getId(), comments.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(comments.get(0).getId()));

        verify(commentService, times(1)).deleteById(comments.get(0).getId());
    }

    @DisplayName("должен изменять комментарий")
    @Test
    void shouldEditCommentAndReturnCommentsList() throws Exception {

        when(commentService.findByBookId(book.getId())).thenReturn(comments);

        CommentDto newComment = comments.get(1);
        newComment.setText("Some Comment");
        when(commentService.update(any(String.class), any(String.class), any(BookDto.class))).thenReturn(newComment);
        mvc.perform(put("/api/v1/book/{bookId}/comment", book.getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(newComment)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(newComment)));

        verify(commentService, times(1)).update(any(String.class), any(String.class), any(BookDto.class));
    }

    @DisplayName("должен создавать новый комментарий")
    @Test
    void shouldInsertCommentAndReturnCommentsList() throws Exception {
        when(commentService.findByBookId(book.getId())).thenReturn(comments);
        CommentDto newComment = new CommentDto(null, "Some Comment", book);
        when(commentService.insert(any(String.class), any(BookDto.class))).thenReturn(newComment);
        mvc.perform(post("/api/v1/book/{bookId}/comment", book.getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(newComment)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(newComment)));

        verify(commentService, times(1)).insert(any(String.class), any(BookDto.class));
    }
}