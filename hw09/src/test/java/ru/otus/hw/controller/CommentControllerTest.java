package ru.otus.hw.controller;

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
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

    private AuthorDto author = new AuthorDto("AuthorId1", "Author 1 FullName");

    private BookDto book = new BookDto("BoolId1", "TestBook1", author.getId(), List.of("GenreId1", "GenreId2"));

    private List<CommentDto> comments = List.of(new CommentDto("CommentId1", "Comment 1", book),
            new CommentDto("CommentId2", "Comment 2", book));

    @DisplayName("должен отображать спиcок список комментариев")
    @Test
    void shouldRenderListPageWithCorrectViewAndModelAttributes() throws Exception {
        when(bookService.findById(book.getId())).thenReturn(Optional.ofNullable(book));
        when(authorService.findAll()).thenReturn(List.of(author));
        when(commentService.findByBookId(book.getId())).thenReturn(comments);

        mvc.perform(get("/comment").param("bookId", book.getId()))
                .andExpect(view().name("commentList"))
                .andExpect(model().attribute("book", book))
                .andExpect(model().attribute("comments", comments))
                .andExpect(model().attribute("authorOptions", List.of(author)));
    }

    @DisplayName("должен отображать ошибку")
    @Test
    void shouldRenderErrorPageWhenBookNotFound() throws Exception {
        when(bookService.findById("someBookId")).thenThrow(new EntityNotFoundException(""));
        mvc.perform(get("/comment").param("bookId", "someBookId"))
                .andExpect(view().name("customError"));
    }

    @DisplayName("должен удалять комментарий")
    @Test
    void shouldDeletAndRenderPageWithCorrectViewAttributes() throws Exception {
        when(bookService.findById(book.getId())).thenReturn(Optional.ofNullable(book));
        when(commentService.findByBookId(book.getId())).thenReturn(comments);
        mvc.perform(get("/deleteById")
                        .param("id", comments.get(0).getId())
                        .param("bookId", comments.get(0).getBook().getId()))
                .andExpect(view().name("redirect:/comment?bookId=" + book.getId()));

        verify(commentService, times(1)).deleteById(comments.get(0).getId());
    }

    @DisplayName("должен изменять комментарий")
    @Test
    void shouldEditAndRenderPageWithCorrectViewAttributes() throws Exception {


        mvc.perform(post("/comment/edit")
                        .param("id", String.valueOf(comments.get(0).getId()))
                        .param("text", String.valueOf(comments.get(0).getText()))
                        .param("bookId", comments.get(0).getBook().getId()))
                .andExpect(view().name("redirect:/comment?bookId=" + comments.get(0).getBook().getId()));

        verify(commentService, times(1)).update(comments.get(0).getId(), comments.get(0).getText(),comments.get(0).getBook().getId());
    }

    @DisplayName("должен создавать новый комментарий")
    @Test
    void shouldInsertAndRenderPageWithCorrectViewAttributes() throws Exception {


        mvc.perform(post("/comment/edit")
                        .param("bookId", comments.get(0).getBook().getId())
                        .param("new-text", "Some Comment"))
                .andExpect(view().name("redirect:/comment?bookId=" + comments.get(0).getBook().getId()));

        verify(commentService, times(1)).insert("Some Comment" ,comments.get(0).getBook().getId());
    }
}
