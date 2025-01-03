package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConvertor;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.security.SecurityConfiguration;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Сервис для комментариев должен")
@DataMongoTest
@Import({CommentServiceImpl.class,
         CommentConvertor.class})
@Transactional(propagation = Propagation.NEVER)
class CommentServiceTest {

    private static final int FIRST_BOOK_COMMENTS_COUNT = 2;

    private static final String FIRST_BOOK_NAME = "BookTitle_1";
    private static final String SECOND_BOOK_NAME = "BookTitle_2";
    private static final String THIRD_BOOK_NAME = "BookTitle_3";
    private static final String WRONG_BOOK_NAME = "WrongBookTitle";


    private static final String FOURTH_COMMENT_TEXT = "BookComment_4";

    private static final String NEW_COMMENT_TEXT = "BookComment_X";

    @Autowired
    private CommentService commentService;

    @Autowired
    private MongoOperations mongoTemplate;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @DisplayName("возвращать комментарий и книгу по его id")
    @Test
    void shouldFindById() {
        Query query = new Query(Criteria.where("text").is(FOURTH_COMMENT_TEXT));
        var expectedComments = mongoTemplate.findOne(query, Comment.class);
        assertThat(expectedComments).isNotNull();

        Optional<CommentDto> expectedComment = commentService.findById(expectedComments.getId());
        assertThat(expectedComment).isNotEmpty().get()
                .hasFieldOrPropertyWithValue("text", FOURTH_COMMENT_TEXT)
                .extracting("book")
                .hasFieldOrPropertyWithValue("title", SECOND_BOOK_NAME);

    }

    @DisplayName("находить все комментарии по id книги")
    @Test
    void shouldFindAllCommentsByBookId() {
        Query query = new Query(Criteria.where("title").is(SECOND_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class);
        assertThat(expectedBook).isNotNull();

        List<CommentDto> expectedComments = commentService.findByBookId(expectedBook.getId());
        assertThat(expectedComments).hasSize(FIRST_BOOK_COMMENTS_COUNT)
                .allMatch(commentDto -> SECOND_BOOK_NAME.equals(commentDto.getBook().getTitle()))
                .anyMatch(commentDto -> FOURTH_COMMENT_TEXT.equals(commentDto.getText()));
    }

    @DisplayName("добавлять новые комментарии к книгам")
    @Test
    void shouldInsertComment() {

        Query query = new Query(Criteria.where("title").is(THIRD_BOOK_NAME));
        var book = mongoTemplate.findOne(query, Book.class);
        assertThat(book).isNotNull();

        Query queryComments = new Query(Criteria.where("book._id").is(book.getId()));
        var comments = mongoTemplate.find(queryComments, Comment.class);
        assertThat(comments).isNotEmpty();

        int beforeCommentCount = comments.size();

        CommentDto commentDto = commentService.insert(NEW_COMMENT_TEXT, book.getId());
        assertThat(commentDto).isNotNull()
                .hasFieldOrPropertyWithValue("text", NEW_COMMENT_TEXT);

        var returnedComments = mongoTemplate.find(queryComments, Comment.class);


        assertThat(returnedComments)
                .isNotEmpty()
                .hasSize(beforeCommentCount + 1)
                .anyMatch(comment -> NEW_COMMENT_TEXT.equals(comment.getText()))
                .allMatch(comment -> THIRD_BOOK_NAME.equals(comment.getBook().getTitle()));
    }

    @DisplayName("изменять комментарии")
    @Test
    void shouldUpdateComment() {


        var expectedComment = getFirstCommentByBookTitle(THIRD_BOOK_NAME);

        CommentDto returnedComment = commentService.update(expectedComment.getId(), NEW_COMMENT_TEXT, expectedComment.getBook().getId());
        assertThat(returnedComment).isNotNull();

        Query queryComments = new Query(Criteria.where("id").is(expectedComment.getId()));
         expectedComment = mongoTemplate.findOne(queryComments, Comment.class);

        assertThat(expectedComment)
                .isNotNull()
                .hasFieldOrPropertyWithValue("text", NEW_COMMENT_TEXT)
                .extracting(Comment::getBook)
                .hasFieldOrProperty("title")
                .isNotEqualTo(FIRST_BOOK_NAME);
    }

    @DisplayName("удалять комментарии по id")
    @Test
    void shouldDeleteCommentById() {
        var expectedComment = getFirstCommentByBookTitle(THIRD_BOOK_NAME);

        commentService.deleteById(expectedComment.getId());

        Query queryComments = new Query(Criteria.where("id").is(expectedComment.getId()));
        var returnedComment = mongoTemplate.findOne(queryComments, Comment.class);

        assertThat(returnedComment).isNull();
    }

    @DisplayName("должен отображать автора")
    @Test
    void shouldFindAuthorInBookById() {
        var comment = getFirstCommentByBookTitle(SECOND_BOOK_NAME);
        Optional<CommentDto> returnedComment = commentService.findById(comment.getId());

        assertThat(returnedComment).isNotEmpty();

        assertThat(returnedComment.get().getBook()).isNotNull().extracting(BookDto::getAuthorId)
                .hasToString(comment.getBook().getAuthor().getId());
    }

    @DisplayName("не должен отображать жанры")
    @Test
    void shouldFindNoneGenresInBook() {
        var comment = getFirstCommentByBookTitle(SECOND_BOOK_NAME);
        Optional<CommentDto> returnedComment = commentService.findById(comment.getId());

        assertThat(returnedComment).isNotEmpty();

        assertThat(returnedComment.get())
                .extracting(CommentDto::getBook)
                .isNotNull()
                .extracting(BookDto::getGenreIds)
                .isNotNull();
        assertThat(returnedComment.get().getBook().getGenreIds())
                .isEmpty();

    }

    @DisplayName("должен выдавать ошибку если книги нет ")
    @Test
    void shouldReturnExceptionWhenBookIsNotFound() {

        var comment = getFirstCommentByBookTitle(SECOND_BOOK_NAME);

        assertThatThrownBy(() -> { commentService.update(comment.getId(), NEW_COMMENT_TEXT, WRONG_BOOK_NAME); })
                . isInstanceOf(EntityNotFoundException.class);
    }

    private Comment getFirstCommentByBookTitle(String title) {
        Query query = new Query(Criteria.where("title").is(title));
        var book = mongoTemplate.findOne(query, Book.class);
        assertThat(book).isNotNull();

        Query queryComments = new Query(Criteria.where("book._id").is(book.getId()));
        var bookComments = mongoTemplate.find(queryComments, Comment.class);

        assertThat(bookComments).isNotEmpty();
        return bookComments.get(0);
    }
}