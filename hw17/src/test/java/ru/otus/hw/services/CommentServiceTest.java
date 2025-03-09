
package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConvertor;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@DisplayName("Сервис для комментариев должен")
@WebMvcTest(value = CommentServiceImpl.class)
@Import({CommentConvertor.class,
        BookConverter.class,
        AuthorConverter.class})
@TestPropertySource(properties = "mongock.enabled=false")
class CommentServiceTest {

    private static final String NEW_COMMENT_TEXT = "BookComment_X";

    private Author author = new Author("1", "Author 1 FullName");
    private List<Genre> genres = List.of(new Genre("1", "Genre1"),
            new Genre("2", "Genre2"),
            new Genre("3", "Genre3"),
            new Genre("4", "Genre4"));

    private Book book = new Book("1", "TestBook1", author, List.of(genres.get(0), genres.get(1)));

    private List<Comment> comments = List.of(new Comment("1", "Comment 1", book),
            new Comment("2", "Comment 2", book));

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private BookConverter bookConverter;

    @DisplayName("возвращать комментарий и книгу по его id")
    @Test
    void shouldFindById() {
        var expectedComment = comments.get(0);
        when(commentRepository.findById(expectedComment.getId())).thenReturn(Optional.of(expectedComment));

        Optional<CommentDto> returnedComment = commentService.findById(expectedComment.getId());

        assertThat(returnedComment).isNotEmpty().get()
                .hasFieldOrPropertyWithValue("text", expectedComment.getText())
                .extracting("book")
                .hasFieldOrPropertyWithValue("title", expectedComment.getBook().getTitle());

    }

    @DisplayName("находить все комментарии по id книги")
    @Test
    void shouldFindAllCommentsByBookId() {
        when(commentRepository.findByBookId(book.getId())).thenReturn(comments);

        List<CommentDto> expectedComments = commentService.findByBookId(book.getId());
        assertThat(expectedComments).hasSize(comments.size())
                .allMatch(commentDto -> book.getTitle().equals(commentDto.getBook().getTitle()))
                .anyMatch(commentDto -> comments.get(0).getText().equals(commentDto.getText()));
    }

    @DisplayName("добавлять новые комментарии к книгам")
    @Test
    void shouldInsertComment() {

        when(bookRepository.findById(book.getId()))
                .thenReturn(Optional.of(book));


        Comment firstComment = comments.get(0);

        Comment newComment = firstComment;
        newComment.setId(null);
        newComment.setText(NEW_COMMENT_TEXT);

        when(commentRepository.save(newComment))
                .thenReturn((firstComment));

        CommentDto commentDto = commentService.insert(NEW_COMMENT_TEXT, bookConverter.bookToDto(book));
        assertThat(commentDto).isNotNull()
                .hasFieldOrPropertyWithValue("text", NEW_COMMENT_TEXT);
    }

    @DisplayName("изменять комментарии")
    @Test
    void shouldUpdateComment() {


        when(bookRepository.findById(book.getId()))
                .thenReturn(Optional.of(book));


        Comment expectedComment = comments.get(0);

        Comment newComment = expectedComment;

        newComment.setText(NEW_COMMENT_TEXT);

        when(commentRepository.findById(newComment.getId()))
                .thenReturn(Optional.of(expectedComment));

        when(commentRepository.save(newComment))
                .thenReturn((newComment));

        CommentDto returnedComment = commentService.update(expectedComment.getId(),
                NEW_COMMENT_TEXT,
                bookConverter.bookToDto(expectedComment.getBook()));
        assertThat(returnedComment).isNotNull();


        assertThat(returnedComment)
                .isNotNull()
                .hasFieldOrPropertyWithValue("text", NEW_COMMENT_TEXT)
                .extracting(CommentDto::getBook)
                .hasFieldOrProperty("title")
                .isNotEqualTo(book.getTitle());
    }

    @DisplayName("удалять комментарии по id")
    @Test
    void shouldDeleteCommentById() {
        var expectedComment = comments.get(0);

        commentService.deleteById(expectedComment.getId());

        verify(commentRepository, times(1)).deleteById(expectedComment.getId());
    }

    @DisplayName("должен отображать автора")
    @Test
    void shouldFindAuthorInBookById() {
        var comment = comments.get(0);

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        Optional<CommentDto> returnedComment = commentService.findById(comment.getId());

        assertThat(returnedComment).isNotEmpty();

        assertThat(returnedComment.get().getBook()).isNotNull().extracting(BookDto::getAuthor)
                .extracting(AuthorDto::getId)
                .isEqualTo(comment.getBook().getAuthor().getId());
    }

    @DisplayName("не должен отображать жанры")
    @Test
    void shouldFindNoneGenresInBook() {
        var comment = comments.get(0);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        Optional<CommentDto> returnedComment = commentService.findById(comment.getId());

        assertThat(returnedComment).isNotEmpty();

        assertThat(returnedComment.get())
                .extracting(CommentDto::getBook)
                .isNotNull()
                .extracting(BookDto::getGenres)
                .isNotNull();
        assertThat(returnedComment.get().getBook().getGenres())
                .isEmpty();

    }

    @DisplayName("должен выдавать ошибку если книги нет ")
    @Test
    void shouldReturnExceptionWhenBookIsNotFound() {

        var comment = comments.get(0);
        var wrongBook = book;
        wrongBook.setId("-1");
        assertThatThrownBy(() -> {
            commentService.update(comment.getId(), NEW_COMMENT_TEXT, bookConverter.bookToDto(wrongBook));
        })
                .isInstanceOf(EntityNotFoundException.class);
    }
}
