
package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для комментариев должен")
@SpringBootTest
@Transactional(propagation = Propagation.NEVER)
class CommentServiceTest {

    private static final int FIRST_BOOK_COMMENTS_COUNT = 2;

    private static final String FIRST_BOOK_NAME = "BookTitle_1";
    private static final String SECOND_BOOK_NAME = "BookTitle_2";
    private static final String THIRD_BOOK_NAME = "BookTitle_3";
    private static final String WRONG_BOOK_NAME = "WrongBookTitle";


    private static final String FIRST_COMMENT_TEXT = "BookComment_1";

    private static final String NEW_COMMENT_TEXT = "BookComment_X";

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReactiveMongoOperations mongoTemplate;

    @Autowired
    private BookConverter bookConverter;

    @DisplayName("возвращать комментарий и книгу по его id")
    @Test
    void shouldFindById() {
         var expectedComments = getFirstCommentByBookTitle(SECOND_BOOK_NAME);
        assertThat(expectedComments).isNotNull();

        Mono<CommentDto> expectedComment = commentService.findById(expectedComments.getId());

        StepVerifier
                .create(expectedComment)
                .assertNext(comment -> {
                    assertThat(comment)
                            .isNotNull()
                            .hasFieldOrPropertyWithValue("text", FIRST_COMMENT_TEXT)
                            .extracting("book")
                            .hasFieldOrPropertyWithValue("title", SECOND_BOOK_NAME);
                        }
                )
                .expectComplete()
                .verify();


    }

    @DisplayName("находить все комментарии по id книги")
    @Test
    void shouldFindAllCommentsByBookId() {
        Query query = new Query(Criteria.where("title").is(SECOND_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class).block();
        assertThat(expectedBook).isNotNull();

        Flux<CommentDto> expectedComments = commentService.findByBookId(expectedBook.getId());

        StepVerifier
                .create(expectedComments)
                .expectNextCount(FIRST_BOOK_COMMENTS_COUNT)
                .thenConsumeWhile(commentDto -> SECOND_BOOK_NAME.equals(commentDto.getBook().getTitle()))
                .expectComplete()
                .verify();


    }

    @DisplayName("добавлять новые комментарии к книгам")
    @Test
    void shouldInsertComment() {

        Query query = new Query(Criteria.where("title").is(THIRD_BOOK_NAME));
        var book = mongoTemplate.findOne(query, Book.class).block();
        assertThat(book).isNotNull();

        Query queryComments = new Query(Criteria.where("book._id").is(book.getId()));
        var comments = mongoTemplate.find(queryComments, Comment.class).collectList().block();
        assertThat(comments).isNotEmpty();

        int beforeCommentCount = comments.size();

        Mono<CommentDto> expectedComment = commentService.insert(NEW_COMMENT_TEXT, bookConverter.bookToDto(book));
        StepVerifier
                .create(expectedComment)
                .assertNext(comment -> {
                    assertThat(comment).isNotNull()
                            .hasFieldOrPropertyWithValue("text", NEW_COMMENT_TEXT);
                        }
                )
                .expectComplete()
                .verify();

        var returnedComments = mongoTemplate.find(queryComments, Comment.class).collectList().block();


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

        Mono<CommentDto> returnedComment = commentService.update(expectedComment.getId(),
                NEW_COMMENT_TEXT,
                bookConverter.bookToDto(expectedComment.getBook()));
        StepVerifier
                .create(returnedComment)
                .assertNext(comment ->   assertThat(comment).isNotNull())
                .expectComplete()
                .verify();

        Query queryComments = new Query(Criteria.where("id").is(expectedComment.getId()));
        expectedComment = mongoTemplate.findOne(queryComments, Comment.class).block();

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
        var returnedComment = mongoTemplate.findOne(queryComments, Comment.class).block();

        assertThat(returnedComment).isNull();
    }

    @DisplayName("должен отображать автора")
    @Test
    void shouldFindAuthorInBookById() {
        var firstCommentByBookTitle = getFirstCommentByBookTitle(SECOND_BOOK_NAME);

        Mono<CommentDto> returnedComment = commentService.findById(firstCommentByBookTitle.getId());

        StepVerifier
                .create(returnedComment)
                .assertNext(comment -> {
                    assertThat(comment).isNotNull();

                    assertThat(comment.getBook())
                            .isNotNull()
                            .extracting(BookDto::getAuthor)
                            .extracting(AuthorDto::getId)
                            .hasToString(comment.getBook().getAuthor().getId());
                })
                .expectComplete()
                .verify();
    }

    @DisplayName("не должен отображать жанры")
    @Test
    void shouldFindNoneGenresInBook() {
        var firstCommentByBookTitle = getFirstCommentByBookTitle(SECOND_BOOK_NAME);

        Mono<CommentDto> returnedComment = commentService.findById(firstCommentByBookTitle.getId());

        StepVerifier
                .create(returnedComment)
                .assertNext(comment -> {
                    assertThat(comment).isNotNull();

                    assertThat(comment)
                            .extracting(CommentDto::getBook)
                            .isNotNull()
                            .extracting(BookDto::getGenres)
                            .isNotNull();
                    assertThat(comment.getBook().getGenres())
                            .isEmpty();
                })
                .expectComplete()
                .verify();
    }

    @DisplayName("должен выдавать ошибку если книги нет ")
    @Test
    void shouldReturnExceptionWhenBookIsNotFound() {

        var firstCommentByBookTitle = getFirstCommentByBookTitle(SECOND_BOOK_NAME);

        var wrongBook = firstCommentByBookTitle.getBook();
        wrongBook.setId(WRONG_BOOK_NAME);
        wrongBook.setTitle(WRONG_BOOK_NAME);
        Mono<CommentDto> commentDtoMono = commentService.update(firstCommentByBookTitle.getId(), NEW_COMMENT_TEXT, bookConverter.bookToDto(wrongBook));

        StepVerifier
                .create(commentDtoMono)
                .expectError()
                .verify();
    }

    private Comment getFirstCommentByBookTitle(String title) {
        Query query = new Query(Criteria.where("title").is(title));
        var book = mongoTemplate.findOne(query, Book.class).block();
        assertThat(book).isNotNull();

        Query queryComments = new Query(Criteria.where("book._id").is(book.getId()));
        var bookComments = mongoTemplate.find(queryComments, Comment.class).collectList().block();

        assertThat(bookComments).isNotEmpty();
        return bookComments.get(0);
    }
}
