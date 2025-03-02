package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BookController.class)
@TestPropertySource(properties = "mongock.enabled=false")
@Slf4j
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

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
        when(bookService.findAll()).thenReturn(books);
        mvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(books)));
    }



    @DisplayName("должен возвращать книгу по ид ")
    @Test
    void shouldReturnStatusOkAndCorrectBookById() throws Exception {
        BookDto expectedBook = books.get(0);
        when(bookService.findById(expectedBook.getId())).thenReturn(Optional.of(expectedBook));
        mvc.perform(get("/api/v1/book/{id}", expectedBook.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedBook)));
    }


    @DisplayName("должен отображать ошибку")
    @Test
    void shouldReturnErrorWhenBookNotFound() throws Exception {
        when(bookService.findById("bookId")).thenThrow(new EntityNotFoundException(""));
        mvc.perform(get("/api/v1/book/{id}", books.get(0).getId() + "wrong"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Entity not found("));
    }

    @DisplayName("должен изменять книгу")
    @Test
    void shouldUpdateBookAndReturnNewBook() throws Exception {
        BookDto expectedBook = books.get(0);
        when(bookService.findById(expectedBook.getId()))
                .thenReturn(Optional.of(expectedBook));

        BookDto newBook = new BookDto(expectedBook.getId(),
                "New Title",
                expectedBook.getAuthor(),
                expectedBook.getGenres());

        when(bookService.update(any(String.class),
                any(String.class),
                any(AuthorDto.class),
                any(HashSet.class)))
                .thenReturn(newBook);

        mvc.perform(put("/api/v1/book")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(newBook)));

        verify(bookService, times(1))
                .update(any(String.class),
                        any(String.class),
                        any(AuthorDto.class),
                        any(HashSet.class));
    }

}