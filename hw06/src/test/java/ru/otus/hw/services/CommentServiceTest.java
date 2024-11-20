package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConvertor;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Сервис для комментариев должен")
@DataJpaTest
@Import({CommentServiceImpl.class,
        JpaCommentRepository.class,
        JpaBookRepository.class,
        CommentConvertor.class})
@Transactional(propagation = Propagation.NEVER)
class CommentServiceTest {

    private static final long FIRST_BOOK_ID = 1L;
    private static final long SECOND_BOOK_ID = 2L;
    private static final long THIRD_BOOK_ID = 3L;
    private static final long WRONG_BOOK_ID = -1L;

    private static final int SECOND_BOOK_COMMENTS_COUNT = 2;

    private static final String FIRST_BOOK_NAME = "BookTitle_1";
    private static final String SECOND_BOOK_NAME = "BookTitle_2";

    private static final String SECOND_AUTHOR_FULL_NAME = "Author_2";

    private static final long FIRST_COMMENT_ID = 1L;
    private static final long SECOND_COMMENT_ID = 2L;
    private static final long THIRD_COMMENT_ID = 3L;

    private static final String FIRST_COMMENT_TEXT = "BookComment_1";
    private static final String FOURTH_COMMENT_TEXT = "BookComment_4";

    private static final String NEW_COMMENT_TEXT = "BookComment_5";

    private static final String THIRD_GENRE_NAME = "Genre_3";

    @Autowired
    private CommentService commentService;

    @DisplayName("возвращать комментарий и книгу по его id")
    @Test
    void shouldFindById() {
        Optional<CommentDto> expectedComment = commentService.findById(FIRST_COMMENT_ID);
        assertThat(expectedComment).isNotEmpty().get()
                .hasFieldOrPropertyWithValue("text", FIRST_COMMENT_TEXT)
                .extracting("book")
                .hasFieldOrPropertyWithValue("title", FIRST_BOOK_NAME);

    }

    @DisplayName("находить все комментарии по id книги")
    @Test
    void shouldFindAllCommentsByBookId() {
        List<CommentDto> expectedComments = commentService.findByBookId(SECOND_BOOK_ID);
        assertThat(expectedComments).hasSize(SECOND_BOOK_COMMENTS_COUNT)
                .allMatch(commentDto -> SECOND_BOOK_NAME.equals(commentDto.getBook().getTitle()))
                .anyMatch(commentDto -> FOURTH_COMMENT_TEXT.equals(commentDto.getText()));
    }

    @DisplayName("добавлять новые комментарии к книгам")
    @Test
    void shouldInsertComment() {

        List<CommentDto> expectedComments = commentService.findByBookId(THIRD_BOOK_ID);

        int beforeCommentCount = expectedComments.size();

        CommentDto commentDto = commentService.insert(NEW_COMMENT_TEXT, THIRD_BOOK_ID);
        assertThat(commentDto).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .hasFieldOrPropertyWithValue("text", NEW_COMMENT_TEXT);

        expectedComments = commentService.findByBookId(THIRD_BOOK_ID);

        assertThat(expectedComments)
                .isNotEmpty()
                .hasSize(beforeCommentCount + 1)
                .anyMatch(comment -> NEW_COMMENT_TEXT.equals(comment.getText()))
                .allMatch(comment -> THIRD_BOOK_ID == comment.getBook().getId());
    }

    @DisplayName("изменять комментарии")
    @Test
    void shouldUpdateBook() {
        Optional<CommentDto> expectedComment = commentService.findById(THIRD_COMMENT_ID);
        assertThat(expectedComment).isNotEmpty();
        long beforeUpdateBookId = expectedComment.get().getBook().getId();

        CommentDto returnedBook = commentService.update(THIRD_COMMENT_ID, NEW_COMMENT_TEXT, FIRST_BOOK_ID);
        assertThat(returnedBook).isNotNull();

        expectedComment = commentService.findById(THIRD_COMMENT_ID);

        assertThat(expectedComment).isNotEmpty();

        assertThat(expectedComment.get())
                .isNotNull()
                .hasFieldOrPropertyWithValue("text", NEW_COMMENT_TEXT)
                .extracting(CommentDto::getBook)
                .hasFieldOrProperty("id")
                .isNotEqualTo(beforeUpdateBookId);
    }

    @DisplayName("удалять комментарии по id")
    @Test
    void shouldDeleteCommentById() {
        commentService.deleteById(THIRD_COMMENT_ID);
        Optional<CommentDto> expectedComment = commentService.findById(THIRD_COMMENT_ID);
        assertThat(expectedComment).isEmpty();
    }

    @DisplayName("должен отображать автора")
    @Test
    void shouldFindAuthInBookById() {
        Optional<CommentDto> returnedComment = commentService.findById(SECOND_COMMENT_ID);

        assertThat(returnedComment).isNotEmpty();

        assertThat(returnedComment.get().getBook()).isNotNull().extracting(BookDto::getAuthor)
                .hasFieldOrPropertyWithValue("fullName", SECOND_AUTHOR_FULL_NAME);
    }

    @DisplayName("не должен отображать жанры")
    @Test
    void shouldFindNoneGenresInBook() {
        Optional<CommentDto> returnedComment = commentService.findById(SECOND_COMMENT_ID);

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
        assertThatThrownBy(() -> { commentService.update(THIRD_COMMENT_ID, NEW_COMMENT_TEXT, WRONG_BOOK_ID); })
                . isInstanceOf(EntityNotFoundException.class);
    }
}