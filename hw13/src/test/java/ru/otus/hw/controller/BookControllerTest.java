package ru.otus.hw.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
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

@AutoConfigureMockMvc(addFilters = false)
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

    private List<BookDto> books = List.of(new BookDto(1L, "TestBook1", 1L, List.of(1L, 2L)),
            new BookDto(2L, "TestBook2", 2L, List.of(3L, 4L)));


    private List<AuthorDto> authors = List.of(new AuthorDto(1L,"Author 1 FullName"),
            new AuthorDto(2L, "Author 2 FullName"));

    private List<GenreDto> genres = List.of(new GenreDto(1L, "Genre1"),
            new GenreDto(2L, "Genre2"),
            new GenreDto(3L, "Genre3"),
            new GenreDto(4L, "Genre4"));

    private List<BookCompliteDto> booksComplite = List.of(new BookCompliteDto(1L, "TestBook1", authors.get(0), List.of(genres.get(0),genres.get(1))),
            new BookCompliteDto(2L, "TestBook2", authors.get(1), List.of(genres.get(2),genres.get(3))));

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
        when(bookService.findById(-1L)).thenThrow(new EntityNotFoundException(""));
        mvc.perform(get("/edit/{id}", -1L))
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
                        .param("id", newBook.getId().toString())
                        .param("title", newBook.getTitle())
                        .param("authorId", newBook.getAuthorId().toString())
                        .param("genreIds", newBook.getGenreIds().stream().map(Object::toString).collect(Collectors.joining(",")))

                )
                .andExpect(view().name("redirect:/"));

        verify(bookService, times(1)).save(newBook.getId(),newBook);
    }

}
