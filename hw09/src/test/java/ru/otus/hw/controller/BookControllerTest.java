package ru.otus.hw.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCompliteDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = BookController.class)
@TestPropertySource(properties = "mongock.enabled=false")
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    private List<BookDto> books = List.of(new BookDto("BoolId1", "TestBook1", "AuthorId1", List.of("GenreId1", "GenreId2")),
            new BookDto("2L", "TestBook2", "AuthorId2", List.of("GenreId3", "GenreId4")));


    private List<AuthorDto> authors = List.of(new AuthorDto("AuthorId1", "Author 1 FullName"),
            new AuthorDto("AuthorId2", "Author 2 FullName"));

    private List<GenreDto> genres = List.of(new GenreDto("GenreId1", "Genre1"),
            new GenreDto("GenreId2", "Genre2"),
            new GenreDto("GenreId3", "Genre3"),
            new GenreDto("GenreId4", "Genre4"));

    private List<BookCompliteDto> booksComplite = List.of(new BookCompliteDto("BoolId1", "TestBook1", authors.get(0), List.of(genres.get(0),genres.get(1))),
            new BookCompliteDto("2L", "TestBook2", authors.get(1), List.of(genres.get(2),genres.get(3))));

    @DisplayName("должен отображать спиcок книг на главной странице")
    @Test
    void shouldRenderListPageWithCorrectViewAndModelAttributes() throws Exception {
        when(bookService.findAll()).thenReturn(booksComplite);
        mvc.perform(get("/"))
                .andExpect(view().name("bookList"))
                .andExpect(model().attribute("books", booksComplite));
    }

    @DisplayName("должен отображать книгу для изменения ")
    @Test
    void shouldRenderEditPageWithCorrectViewAndModelAttributes() throws Exception {
        BookDto expectedBook = books.get(0);
        when(bookService.findById(expectedBook.getId())).thenReturn(Optional.of(expectedBook));
        mvc.perform(get("/edit/{id}", expectedBook.getId()))
                .andExpect(view().name("bookEdit"))
                .andExpect(model().attribute("book", expectedBook));
    }


    @DisplayName("должен отображать ошибку")
    @Test
    void shouldRenderErrorPageWhenBookNotFound() throws Exception {
        when(bookService.findById("bookId")).thenThrow(new EntityNotFoundException(""));
        mvc.perform(get("/edit/{id}", "someBookId"))
                .andExpect(view().name("customError"));
    }

    @DisplayName("должен изменять книгу")
    @Test
    void shouldSaveBookAndRedirectToContextPath() throws Exception {
        BookDto expectedBook = books.get(0);

        BookDto newBook = new BookDto(expectedBook.getId(),
                "New Title",
                expectedBook.getAuthorId(),
                expectedBook.getGenreIds());

        mvc.perform(post("/edit")
                        .param("id", newBook.getId())
                        .param("title", newBook.getTitle())
                        .param("authorId", newBook.getAuthorId())
                        .param("genreIds", newBook.getGenreIds().stream().collect(Collectors.joining(",")))

                )
                .andExpect(view().name("redirect:/"));

        verify(bookService, times(1)).update(expectedBook.getId(),
                "New Title",
                expectedBook.getAuthorId(),
                expectedBook.getGenreIds().stream().collect(Collectors.toSet()));
    }

}
